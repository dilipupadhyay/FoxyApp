<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<f:view>
    <%@ include file="foxyHeader.jsp" %>
    <table class="box" width="100%">
        <tr><td>
                <h:panelGrid id="UpdOrderReserveNoGrid" styleClass="tablebg" width="100%">
                    <%-- Title for Add --%>
                    <h:outputText value="Ref No Reservation " styleClass="smalltitle"/>
                    
                    <h:form id="UpdAddForm">
                    	<h:inputHidden id="reservedNo" value="#{foxyUpdateOrderNoReserve.reservedNo}" />
                        <h:panelGrid id="UpdReserveNoInput" columns="2" columnClasses="FOX_LABEL_COL, FOX_INPUT_COL" width="50%">
							
                            <h:outputText value="Ref No." />
                            <h:outputText id="Ref_No" value="#{foxyUpdateOrderNoReserve.reservedNo}" />
                            
                            <h:outputText value="Year:" />
                            <h:outputText value="#{foxyUpdateOrderNoReserve.orderIdYear}" />

                            <t:outputLabel for="Country" value="Main Factory:" rendered="true"/>
                            <h:panelGroup rendered="true">
                                <h:selectOneMenu id="Country" onchange="modifyRefNo();" styleClass="FOX_INPUT" required="true" 
                                                 value="#{foxyUpdateOrderNoReserve.mainFactoryCode}">

                                    <f:selectItems value="#{listData.factoryList}"/>
                                </h:selectOneMenu>
                                <h:message errorClass="FOX_ERROR" for="Country" showDetail="true" showSummary="true"/>
                            </h:panelGroup>                                                                     

                            <!-- ==================== Input Filed for [Company Name] --START-- ========================= -->
                            <t:outputLabel for="CName" value="Company Name:" />
                            <h:panelGroup >
                                <h:selectOneMenu id="CName" onchange="modifyRefNo();" value="#{foxyUpdateOrderNoReserve.cnameCode}" styleClass="FOX_INPUT" required="true" immediate="false">
                                    <f:selectItems value="#{listData.companyNameList}"/>                                  
                                </h:selectOneMenu>
                                <h:message errorClass="FOX_ERROR" for="CName" showDetail="true" showSummary="true"/>
                            </h:panelGroup>
                            
                            <h:outputText value="Valid For (Days, Max=99):" />
                            <h:outputText value="#{foxyUpdateOrderNoReserve.daysToExpired}" />  
                        </h:panelGrid>  

                        <h:panelGrid id="AddButton" columns="1" columnClasses="FOX_BUTTON" width="50%">
                            <h:panelGroup>
                                <h:commandButton id="Save"  value="Update" action="#{foxyUpdateOrderNoReserve.update}">
                                 </h:commandButton>
                            </h:panelGroup>
                        </h:panelGrid>

                    </h:form>
                </h:panelGrid>
                <%-- End of Update & Add --%>

            </td></tr>
        <tr>
            <td>
                <!-- Report List -->
            </td>
        </tr>
    </table>
    <%@ include file="foxyFooter.jsp" %>
</f:view>

<script>

	function getSelectText(selId) { 
		//alert("ok1"+selId);
	   var sel = document.getElementById(selId);
	  // alert("ok2"+sel);
	   var i = sel.selectedIndex;
	  // alert("ok3"+i);
	   var selected_text = sel.options[i].text;
	  // alert("ok4"+selected_text);
	   return selected_text;
	}
	
	function getSelectValue(selId) { 
		//alert("ok1"+selId);
	   var sel = document.getElementById(selId);
	  // alert("ok2"+sel);
	   var i = sel.selectedIndex;
	  // alert("ok3"+i);
	   var selected_value = sel.options[i].value;
	  // alert("ok4"+selected_text);
	   return selected_value;
	}
	
	
	function modifyRefNo() {
		//alert("ok");
		//alert(getSelectValue("UpdAddForm:CName"));
		var cName = getSelectText("UpdAddForm:CName");
		//alert(getSelectValue("UpdAddForm:Country"));
		var country = getSelectValue("UpdAddForm:Country");
		var cNameFirstChar = cName.substring(0,1);
		//UpdAddForm:Ref_No
		var refNumber = document.getElementById("UpdAddForm:reservedNo").value;
		//alert(refNumber);
		var prefix = refNumber.substring(0, 2);
		//alert(prefix);
		var suffix = "";
		if (refNumber.length > 7)
			suffix = refNumber.substring(4, refNumber.length);
		else {
			suffix = refNumber.substring(3, refNumber.length);
		}
		//alert(suffix);
		
		var finalRefNo = prefix+country+cNameFirstChar+suffix;
		//alert (finalRefNo);
		
		document.getElementById("UpdAddForm:Ref_No").innerHTML=finalRefNo;
		document.getElementById("UpdAddForm:reservedNo").value=finalRefNo;
		//alert(document.getElementById("UpdAddForm:reservedNo").value);
	}
</script>