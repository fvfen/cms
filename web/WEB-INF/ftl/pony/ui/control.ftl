<#--
label
colspan
labelWidth
-->
<#if label!="">
<td width="${labelWidth}%" class="pn-flabel<#if noHeight=='false'> pn-flabel-h</#if>"><#if required!="false"><span class="pn-frequired">*</span></#if><@ss.mt code=label text=label/><#if hasColon="true">${colon}</#if></td><#rt/>
<td<#if colspan!=""> colspan="${colspan?number*2-1}"</#if> width="${width?number-labelWidth?number}%" class="pn-fcontent"><#rt/>
</#if>
<#if help!="" && helpPosition=='1'><div class="pn-fhelp"><@ss.mt code=help text=help/></div></#if>