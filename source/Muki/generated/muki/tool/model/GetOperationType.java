//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-558 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.02.24 at 02:46:52 PM CET 
//


package muki.tool.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for get-operationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="get-operationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="path-param" type="{http://muki/service-description/}path-paramType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="query-param" type="{http://muki/service-description/}query-paramType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="context-param" type="{http://muki/service-description/}context-paramType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="return-type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="http-path" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="serialization-type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "get-operationType", propOrder = {
    "pathParam",
    "queryParam",
    "contextParam"
})
public class GetOperationType {

    @XmlElement(name = "path-param")
    protected List<PathParamType> pathParam;
    @XmlElement(name = "query-param")
    protected List<QueryParamType> queryParam;
    @XmlElement(name = "context-param")
    protected List<ContextParamType> contextParam;
    @XmlAttribute
    protected String name;
    @XmlAttribute(name = "return-type")
    protected String returnType;
    @XmlAttribute(name = "http-path")
    protected String httpPath;
    @XmlAttribute(name = "serialization-type")
    protected String serializationType;

    /**
     * Gets the value of the pathParam property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pathParam property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPathParam().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PathParamType }
     * 
     * 
     */
    public List<PathParamType> getPathParam() {
        if (pathParam == null) {
            pathParam = new ArrayList<PathParamType>();
        }
        return this.pathParam;
    }

    /**
     * Gets the value of the queryParam property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the queryParam property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQueryParam().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QueryParamType }
     * 
     * 
     */
    public List<QueryParamType> getQueryParam() {
        if (queryParam == null) {
            queryParam = new ArrayList<QueryParamType>();
        }
        return this.queryParam;
    }

    /**
     * Gets the value of the contextParam property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the contextParam property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getContextParam().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ContextParamType }
     * 
     * 
     */
    public List<ContextParamType> getContextParam() {
        if (contextParam == null) {
            contextParam = new ArrayList<ContextParamType>();
        }
        return this.contextParam;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the returnType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnType() {
        return returnType;
    }

    /**
     * Sets the value of the returnType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnType(String value) {
        this.returnType = value;
    }

    /**
     * Gets the value of the httpPath property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHttpPath() {
        return httpPath;
    }

    /**
     * Sets the value of the httpPath property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHttpPath(String value) {
        this.httpPath = value;
    }

    /**
     * Gets the value of the serializationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSerializationType() {
        return serializationType;
    }

    /**
     * Sets the value of the serializationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSerializationType(String value) {
        this.serializationType = value;
    }

}
