
package ec.edu.monster.cliweb.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para cuentaResumen complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>{@code
 * <complexType name="cuentaResumen">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="codigoCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="codigoCuenta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="estado" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="moneda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="nombreCliente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="saldo" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cuentaResumen", propOrder = {
    "codigoCliente",
    "codigoCuenta",
    "estado",
    "moneda",
    "nombreCliente",
    "saldo"
})
public class CuentaResumen {

    protected String codigoCliente;
    protected String codigoCuenta;
    protected String estado;
    protected String moneda;
    protected String nombreCliente;
    protected double saldo;

    /**
     * Obtiene el valor de la propiedad codigoCliente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoCliente() {
        return codigoCliente;
    }

    /**
     * Define el valor de la propiedad codigoCliente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoCliente(String value) {
        this.codigoCliente = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoCuenta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodigoCuenta() {
        return codigoCuenta;
    }

    /**
     * Define el valor de la propiedad codigoCuenta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodigoCuenta(String value) {
        this.codigoCuenta = value;
    }

    /**
     * Obtiene el valor de la propiedad estado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Define el valor de la propiedad estado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstado(String value) {
        this.estado = value;
    }

    /**
     * Obtiene el valor de la propiedad moneda.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMoneda() {
        return moneda;
    }

    /**
     * Define el valor de la propiedad moneda.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMoneda(String value) {
        this.moneda = value;
    }

    /**
     * Obtiene el valor de la propiedad nombreCliente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombreCliente() {
        return nombreCliente;
    }

    /**
     * Define el valor de la propiedad nombreCliente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombreCliente(String value) {
        this.nombreCliente = value;
    }

    /**
     * Obtiene el valor de la propiedad saldo.
     * 
     */
    public double getSaldo() {
        return saldo;
    }

    /**
     * Define el valor de la propiedad saldo.
     * 
     */
    public void setSaldo(double value) {
        this.saldo = value;
    }

}
