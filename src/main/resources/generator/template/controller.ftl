
package ${packageName}.controller.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import ${packageName}.controller.BaseWebController;
import ${packageName}.entity.${tableUpperCamel};
import ${packageName}.form.admin.${tableUpperCamel}Form;
import ${packageName}.repository.${tableUpperCamel}Repository;

@Controller
@RequestMapping(value = "/admin/${tableLowerCamel}")
public class Admin${tableUpperCamel}Controller extends BaseWebController {
	
	@Autowired
	private ${tableUpperCamel}Repository repository;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(@PageableDefault(size = 50) Pageable pageable, ${tableUpperCamel}Form form, Model model,
		HttpServletRequest req, HttpServletResponse res) {
		
		model.addAttribute("entities", this.repository.findAll(pageable));
		
		return "admin/${tableLowerCamel}/index";
	}
	
	@RequestMapping(value = "${entity.idPathExpression}", method = RequestMethod.GET)
	public String detail(${entity.idControllerParamExpression}${tableUpperCamel}Form form, Model model, HttpServletRequest req,
		HttpServletResponse res) {
		
<#if entity.id.embeddedId>
		model.addAttribute("entity", this.repository.findOne(new ${tableUpperCamel}.Id(<#list entity.id.fields as field>${field.name}<#sep>, </#list>)));
<#else>
		model.addAttribute("entity", this.repository.findOne(${entity.id.fields[0].name}));
</#if>
		
		return "admin/${tableLowerCamel}/detail";
	}
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String save(@ModelAttribute ${tableUpperCamel}Form form, RedirectAttributesModelMap model, HttpServletRequest req,
		HttpServletResponse res) {
		
		${tableUpperCamel} entity = new ${tableUpperCamel}();
		BeanUtils.copyProperties(form, entity);
		this.repository.save(entity);
		
		return this.redirectReferer(req);
	}
	
	@RequestMapping(value = "${entity.idPathExpression}", method = RequestMethod.PUT)
	public String update(${entity.idControllerParamExpression}${tableUpperCamel}Form form, RedirectAttributesModelMap model,
		HttpServletRequest req, HttpServletResponse res) {
		
<#if entity.id.embeddedId>
		${tableUpperCamel} entity = this.repository.findOne(new ${tableUpperCamel}.Id(<#list entity.id.fields as field>${field.name}<#sep>, </#list>)));
<#else>
		${tableUpperCamel} entity = this.repository.findOne(${entity.id.fields[0].name});
</#if>
		BeanUtils.copyProperties(form, entity, "id", "createDate", "updateDate");
		this.repository.save(entity);
		
		return this.redirectReferer(req);
	}
	
	@RequestMapping(value = "${entity.idPathExpression}", method = RequestMethod.DELETE)
	public String delete(${entity.idControllerParamExpression}RedirectAttributesModelMap model, HttpServletRequest req,
		HttpServletResponse res) {
		
<#if entity.id.embeddedId>
		this.repository.delete(new ${tableUpperCamel}.Id(<#list entity.id.fields as field>${field.name}<#sep>, </#list>)));
<#else>
		this.repository.delete(${entity.id.fields[0].name});
</#if>
		
		return this.redirectReferer(req);
	}
}
