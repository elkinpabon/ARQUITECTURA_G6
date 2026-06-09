
package ec.edu.monster.cliweb.ws;

import javax.xml.namespace.QName;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ec.edu.monster.cliweb.ws package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private static final QName _ListarMovimientos_QNAME = new QName("http://ws.monster.edu.ec/", "listarMovimientos");
    private static final QName _ListarMovimientosResponse_QNAME = new QName("http://ws.monster.edu.ec/", "listarMovimientosResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ec.edu.monster.cliweb.ws
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ListarMovimientos }
     * 
     * @return
     *     the new instance of {@link ListarMovimientos }
     */
    public ListarMovimientos createListarMovimientos() {
        return new ListarMovimientos();
    }

    /**
     * Create an instance of {@link ListarMovimientosResponse }
     * 
     * @return
     *     the new instance of {@link ListarMovimientosResponse }
     */
    public ListarMovimientosResponse createListarMovimientosResponse() {
        return new ListarMovimientosResponse();
    }

    /**
     * Create an instance of {@link MovimientoModel }
     * 
     * @return
     *     the new instance of {@link MovimientoModel }
     */
    public MovimientoModel createMovimientoModel() {
        return new MovimientoModel();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarMovimientos }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarMovimientos }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "listarMovimientos")
    public JAXBElement<ListarMovimientos> createListarMovimientos(ListarMovimientos value) {
        return new JAXBElement<>(_ListarMovimientos_QNAME, ListarMovimientos.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ListarMovimientosResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ListarMovimientosResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://ws.monster.edu.ec/", name = "listarMovimientosResponse")
    public JAXBElement<ListarMovimientosResponse> createListarMovimientosResponse(ListarMovimientosResponse value) {
        return new JAXBElement<>(_ListarMovimientosResponse_QNAME, ListarMovimientosResponse.class, null, value);
    }

}
