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
 * <p>Java class for controllerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="controllerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="get-operation" type="{http://muki/service-description/}get-operationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="post-operation" type="{http://muki/service-description/}post-operationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="put-operation" type="{http://muki/service-description/}put-operationType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="delete-operation" type="{http://muki/service-description/}delete-operationType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="http-path" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "controllerType", propOrder = {
    "getOperation",
    "postOperation",
    "putOperation",
    "deleteOperation"
})
public class ControllerType {

    @XmlElement(name = "get-operation")
    protected List<GetOperationType> getOperation;
    @XmlElement(name = "post-operation")
    protected List<PostOperationType> postOperation;
    @XmlElement(name = "put-operation")
    protected List<PutOperationType> putOperation;
    @XmlElement(name = "delete-operation")
    protected List<DeleteOperationType> deleteOperation;
    @XmlAttribute
    protected String name;
    @XmlAttribute(name = "http-path")
    protected String httpPath;

    /**
     * Gets the value of the getOperation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the getOperation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGetOperation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GetOperationType }
     * 
     * 
     */
    public List<GetOperationType> getGetOperation() {
        if (getOperation == null) {
            getOperation = new ArrayList<GetOperationType>();
        }
        return this.getOperation;
    }

    /**
     * Gets the value of the postOperation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the postOperation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPostOperation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PostOperationType }
     * 
     * 
     */
    public List<PostOperationType> getPostOperation() {
        if (postOperation == null) {
            postOperation = new ArrayList<PostOperationType>();
        }
        return this.postOperation;
    }

    /**
     * Gets the value of the putOperation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the putOperation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPutOperation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PutOperationType }
     * 
     * 
     */
    public List<PutOperationType> getPutOperation() {
        if (putOperation == null) {
            putOperation = new ArrayList<PutOperationType>();
        }
        return this.putOperation;
    }

    /**
     * Gets the value of the deleteOperation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the deleteOperation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDeleteOperation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DeleteOperationType }
     * 
     * 
     */
    public List<DeleteOperationType> getDeleteOperation() {
        if (deleteOperation == null) {
            deleteOperation = new ArrayList<DeleteOperationType>();
        }
        return this.deleteOperation;
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

}
