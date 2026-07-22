package ec.edu.monster.servicio;

import jakarta.jws.WebParam;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

final class SoapClientProxy implements InvocationHandler {

    private static final String NS = "http://ws.monster.edu.ec/";
    private static final String SOAP_NS = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(15);
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5)).build();

    private final Class<?> serviceInterface;
    private final URI endpoint;

    private SoapClientProxy(Class<?> serviceInterface, String endpoint) {
        this.serviceInterface = serviceInterface;
        this.endpoint = URI.create(endpoint);
    }

    static <T> T create(Class<T> type, String endpoint) {
        Object proxy = Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                new SoapClientProxy(type, endpoint));
        return type.cast(proxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return objectMethod(proxy, method, args);
        }

        try {
            RequestWrapper requestInfo = required(method.getAnnotation(RequestWrapper.class),
                    "@RequestWrapper", method);
            ResponseWrapper responseInfo = required(method.getAnnotation(ResponseWrapper.class),
                    "@ResponseWrapper", method);
            ClassLoader loader = serviceInterface.getClassLoader();
            Class<?> requestType = Class.forName(requestInfo.className(), true, loader);
            Class<?> responseType = Class.forName(responseInfo.className(), true, loader);
            Object requestWrapper = requestType.getConstructor().newInstance();
            populate(method, requestWrapper, args == null ? new Object[0] : args);

            String payload = marshal(requestType, requestWrapper, requestInfo.localName());
            HttpRequest request = HttpRequest.newBuilder(endpoint)
                    .timeout(REQUEST_TIMEOUT)
                    .header("Content-Type", "text/xml; charset=UTF-8")
                    .header("SOAPAction", "\"\"")
                    .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                    .build();
            HttpResponse<String> response = HTTP.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            return readResponse(response, responseType);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Llamada SOAP interrumpida a " + endpoint, e);
        } catch (IOException e) {
            String detail = e.getMessage();
            if (detail == null || detail.isBlank()) {
                detail = e instanceof java.net.ConnectException
                        ? "conexion rechazada" : e.getClass().getSimpleName();
            }
            throw new RuntimeException("Error de conexion HTTP con " + endpoint + ": " + detail, e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error invocando SOAP en " + endpoint + ": "
                    + e.getMessage(), e);
        }
    }

    private Object objectMethod(Object proxy, Method method, Object[] args) {
        return switch (method.getName()) {
            case "toString" -> "SOAP proxy " + serviceInterface.getSimpleName() + " -> " + endpoint;
            case "hashCode" -> System.identityHashCode(proxy);
            case "equals" -> proxy == args[0];
            default -> throw new UnsupportedOperationException(method.getName());
        };
    }

    private static <A> A required(A annotation, String name, Method method) {
        if (annotation == null) {
            throw new IllegalStateException(name + " ausente en " + method.getName());
        }
        return annotation;
    }

    private static void populate(Method serviceMethod, Object wrapper, Object[] args)
            throws Exception {
        Annotation[][] annotations = serviceMethod.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            WebParam webParam = null;
            for (Annotation annotation : annotations[i]) {
                if (annotation instanceof WebParam value) {
                    webParam = value;
                    break;
                }
            }
            if (webParam == null) {
                throw new IllegalStateException("@WebParam ausente en parametro " + i);
            }
            String setterName = "set" + Character.toUpperCase(webParam.name().charAt(0))
                    + webParam.name().substring(1);
            Method setter = null;
            for (Method candidate : wrapper.getClass().getMethods()) {
                if (candidate.getName().equals(setterName)
                        && candidate.getParameterCount() == 1) {
                    setter = candidate;
                    break;
                }
            }
            if (setter == null) {
                throw new IllegalStateException("No existe " + setterName + " en "
                        + wrapper.getClass().getName());
            }
            setter.invoke(wrapper, args[i]);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String marshal(Class<?> requestType, Object wrapper, String operation)
            throws Exception {
        JAXBContext context = JAXBContext.newInstance(requestType);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        StringWriter content = new StringWriter();
        marshaller.marshal(new JAXBElement(new QName(NS, operation), requestType, wrapper), content);
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"" + SOAP_NS + "\">"
                + "<soapenv:Header/><soapenv:Body>" + content
                + "</soapenv:Body></soapenv:Envelope>";
    }

    private static Object readResponse(HttpResponse<String> response, Class<?> responseType)
            throws Exception {
        Document document;
        try {
            document = parse(response.body());
        } catch (Exception e) {
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("HTTP " + response.statusCode() + " desde el endpoint SOAP");
            }
            throw new RuntimeException("Respuesta SOAP XML invalida", e);
        }

        NodeList bodies = document.getElementsByTagNameNS(SOAP_NS, "Body");
        if (bodies.getLength() == 0) {
            throw new RuntimeException("Respuesta sin SOAP Body");
        }
        Element content = firstElement(bodies.item(0));
        if (content == null) {
            throw new RuntimeException("SOAP Body vacio");
        }
        if ("Fault".equals(content.getLocalName())) {
            NodeList messages = content.getElementsByTagName("faultstring");
            String message = messages.getLength() == 0
                    ? content.getTextContent().trim() : messages.item(0).getTextContent().trim();
            throw new RuntimeException("SOAP Fault: " + message);
        }
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("HTTP " + response.statusCode() + " desde el endpoint SOAP");
        }

        Object wrapper = JAXBContext.newInstance(responseType).createUnmarshaller()
                .unmarshal(new DOMSource(content), responseType).getValue();
        try {
            return responseType.getMethod("getReturn").invoke(wrapper);
        } catch (NoSuchMethodException e) {
            return responseType.getMethod("isReturn").invoke(wrapper);
        }
    }

    private static Document parse(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
    }

    private static Element firstElement(Node parent) {
        for (Node child = parent.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) child;
            }
        }
        return null;
    }
}
