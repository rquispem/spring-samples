package org.springframework.samples.mvc.form;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.samples.mvc.flash.FlashMap;
import org.springframework.samples.mvc.flash.FlashMap.FlashMessage;
import org.springframework.samples.mvc.flash.FlashMap.FlashMessageType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/form")
public class FormController {

	@RequestMapping(method=RequestMethod.GET)
	public FormBean form(HttpSession session) {
		FormBean form = (FormBean) session.getAttribute("form");
		return form != null ? form : new FormBean();
	}

	@RequestMapping(method=RequestMethod.POST)
	public String processSubmit(@RequestHeader(value="X-Requested-With", required=false) String requestedWith, @Valid FormBean form, BindingResult result, HttpSession session, Model model) {
		if (result.hasErrors()) {
			return null;
		}
		// simply store form bean in the session for demo purposes, typically you would save form bean values to a db
		session.setAttribute("form", form);
		String message = "Form submitted successfully.  Bound " + form;
		// success response handling
		if (isAjaxRequest(requestedWith)) {
			// prepare model for rendering success message in this request			
			model.addAttribute("message", new FlashMessage(FlashMessageType.success, message));
			return null;
		} else {
			// store a success message for rendering on the next request after redirect
			FlashMap.setSuccessMessage(message);
			// redirect back to the form to render the success message along with newly bound values
			return "redirect:/form";			
		}
	}
	
	private boolean isAjaxRequest(String requestedWith) {
		return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
	}
}
