package org.soa.rest.resources;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.soa.common.context.SoaContext;
import org.soa.logger.SoaLogger;
import org.soa.rest.jsonp.JSONPObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.sys.api.croe.SysSoaManger;

import com.alibaba.dubbo.config.annotation.Reference;

@Controller
@RequestMapping
public class UserResources {
	
	@Reference(version = "1.0.0",interfaceClass=SysSoaManger.class,timeout=2000,check=true,lazy=false)
	private SysSoaManger soaManger;

	@ResponseBody
	@RequestMapping(value="/sys/invoker"
					,method={RequestMethod.GET,RequestMethod.POST})
	public JSONPObject invork(@ModelAttribute SoaContext context,HttpServletRequest request,String callback,Map<String,String> param) {
		final long begin  = System.currentTimeMillis();
		final Enumeration<String> names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String key = names.nextElement();
			if(key.intern() == "method".intern() ||key.intern() == "service".intern()) continue;
			context.addAttr(key, request.getParameter(key));
		}
		System.out.println(context.getService());
		context = soaManger.callNoTx(context);
		SoaLogger.debug(getClass(), "service {} in method {}执行时间{}ms",context.getService(),context.getMethod(), System.currentTimeMillis()-begin);
		return new JSONPObject(callback,context);
	}
	
	
	@RequestMapping(value="/views/{url}",method={RequestMethod.GET,RequestMethod.POST})
	public String loginView(@PathVariable("url") String url){
		return url;
	}
	
	
	

}
