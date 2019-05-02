package ${packageName}.web.controller.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import lombok.AllArgsConstructor;
import ${packageName}.data.entity.${tableUpperCamel};
import ${packageName}.data.repository.${tableUpperCamel}Repository;
import ${packageName}.web.form.admin.Admin${tableUpperCamel}Form;

@AllArgsConstructor
@Controller
@RequestMapping(value = "/admin/${tableLowerCamel}")
public class Admin${tableUpperCamel}Controller {
	
	private ${tableUpperCamel}Repository repository;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index(@PageableDefault(size = 50) Pageable pageable, Admin${tableUpperCamel}Form form, Model model,
		HttpServletRequest req, HttpServletResponse res) {
		
<#list entity.id.fields as field>
	<#if field.enumId>
		model.addAttribute("${field.enumInstanceName}s", ${packageName}.type.${field.enumClassName}.values());
	</#if>
</#list>
<#list entity.fields as field>
	<#if field.enumId>
		model.addAttribute("${field.enumInstanceName}s", ${packageName}.type.${field.enumClassName}.values());
	</#if>
</#list>
		model.addAttribute("entities", this.repository.findAll(pageable));
		
		return "admin/${tableLowerCamel}/index";
	}
	
	@RequestMapping(value = "${entity.idPathExpression}", method = RequestMethod.GET)
	public String detail(${entity.idControllerParamExpression}Admin${tableUpperCamel}Form form, Model model, HttpServletRequest req,
		HttpServletResponse res) {
		
<#list entity.id.fields as field>
	<#if field.enumId>
		model.addAttribute("${field.enumInstanceName}s", ${packageName}.type.${field.enumClassName}.values());
	</#if>
</#list>
<#list entity.fields as field>
	<#if field.enumId>
		model.addAttribute("${field.enumInstanceName}s", ${packageName}.type.${field.enumClassName}.values());
	</#if>
</#list>
<#if entity.id.embeddedId>
		model.addAttribute("entity", this.repository.findById(new ${tableUpperCamel}.Id(<#list entity.id.fields as field>${field.name}<#sep>, </#list>)).orElse(null));
<#else>
		model.addAttribute("entity", this.repository.findById(${entity.id.fields[0].name}).orElse(null));
</#if>
		
		return "admin/${tableLowerCamel}/detail";
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String save(@ModelAttribute Admin${tableUpperCamel}Form form, BindingResult bindingResult, RedirectAttributesModelMap model, HttpServletRequest req,
		HttpServletResponse res) {
		
		${tableUpperCamel} entity = new ${tableUpperCamel}();
		BeanUtils.copyProperties(form, entity);
		this.repository.save(entity);
		
		return "redirect:/admin/${tableLowerCamel}";
	}
	
	@RequestMapping(value = "${entity.idPathExpression}", method = RequestMethod.PUT)
	public String update(${entity.idControllerParamExpression}Admin${tableUpperCamel}Form form, BindingResult bindingResult, RedirectAttributesModelMap model,
		HttpServletRequest req, HttpServletResponse res) {
		
<#if entity.id.embeddedId>
		${tableUpperCamel} entity = this.repository.findById(new ${tableUpperCamel}.Id(<#list entity.id.fields as field>${field.name}<#sep>, </#list>)).orElse(null);
<#else>
		${tableUpperCamel} entity = this.repository.findById(${entity.id.fields[0].name}).orElse(null);
</#if>
		BeanUtils.copyProperties(form, entity, "id", "createDatetime", "updateDatetime");
		this.repository.save(entity);
		
		return "redirect:/admin/${tableLowerCamel}";
	}
	
	@RequestMapping(value = "${entity.idPathExpression}", method = RequestMethod.DELETE)
	public String delete(${entity.idControllerParamExpression}RedirectAttributesModelMap model, HttpServletRequest req,
		HttpServletResponse res) {
		
<#if entity.id.embeddedId>
		this.repository.deleteById(new ${tableUpperCamel}.Id(<#list entity.id.fields as field>${field.name}<#sep>, </#list>));
<#else>
		this.repository.deleteById(${entity.id.fields[0].name});
</#if>
		
		return "redirect:/admin/${tableLowerCamel}";
	}
}
