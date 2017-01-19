
package com.cheerz.controller.admin;

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

import com.cheerz.controller.BaseWebController;
import com.cheerz.entity.CheerItem;
import com.cheerz.form.admin.CheerItemForm;
import com.cheerz.repository.CheerItemRepository;

@Controller
@RequestMapping(value = "/admin/cheerItem")
public class AdminCheerItemController extends BaseWebController {
	
	@Autowired
	private CheerItemRepository repository;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(@PageableDefault(size = 50) Pageable pageable, CheerItemForm form, Model model,
		HttpServletRequest req, HttpServletResponse res) {
		
		model.addAttribute("entities", this.repository.findAll(pageable));
		
		return "admin/cheerItem/index";
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String detail(@PathVariable Long id, CheerItemForm form, Model model, HttpServletRequest req,
		HttpServletResponse res) {
		
		model.addAttribute("entity", this.repository.findOne(id));
		
		return "admin/cheerItem/detail";
	}
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String save(@ModelAttribute CheerItemForm form, RedirectAttributesModelMap model, HttpServletRequest req,
		HttpServletResponse res) {
		
		CheerItem entity = new CheerItem();
		BeanUtils.copyProperties(form, entity);
		this.repository.save(entity);
		
		return this.redirectReferer(req);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public String update(@PathVariable Long id, CheerItemForm form, RedirectAttributesModelMap model,
		HttpServletRequest req, HttpServletResponse res) {
		
		CheerItem entity = this.repository.findOne(id);
		BeanUtils.copyProperties(form, entity, "id", "createDate", "updateDate");
		this.repository.save(entity);
		
		return this.redirectReferer(req);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public String delete(@PathVariable Long id, RedirectAttributesModelMap model, HttpServletRequest req,
		HttpServletResponse res) {
		
		this.repository.delete(id);
		
		return this.redirectReferer(req);
	}
}
