package com.qcms.cms.result;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.struts2.views.freemarker.FreemarkerManager;
import org.apache.struts2.views.freemarker.StrutsClassTemplateLoader;
import org.springframework.util.CollectionUtils;

import com.opensymphony.xwork2.util.logging.Logger;
import com.opensymphony.xwork2.util.logging.LoggerFactory;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class QFreemarkerManager extends FreemarkerManager {
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	private Map<String, Object> freemarkerVariables;

	@Override
	public void init(ServletContext servletContext) throws TemplateException {
		config = createConfiguration(servletContext);

		// Set defaults:
		config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		contentType = DEFAULT_CONTENT_TYPE;

		// Process object_wrapper init-param out of order:
		wrapper = createObjectWrapper(servletContext);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Using object wrapper of class "
					+ wrapper.getClass().getName());
		}
		config.setObjectWrapper(wrapper);

		// Process TemplatePath init-param out of order:
		if (templatePath == null) {
			templatePath = servletContext
					.getInitParameter(INITPARAM_TEMPLATE_PATH);
		}
		if (templatePath == null) {
			templatePath = servletContext.getInitParameter("templatePath");
		}

		config.setTemplateLoader(createTemplateLoader(servletContext,
				templatePath));
		if (!CollectionUtils.isEmpty(this.freemarkerVariables)) {
			config.setAllSharedVariables(new SimpleHash(
					this.freemarkerVariables, config.getObjectWrapper()));
		}
		loadSettings(servletContext);
	}


	@Override
	protected TemplateLoader createTemplateLoader(
			ServletContext servletContext, String templatePath) {
		TemplateLoader templatePathLoader = null;

		try {
			if (templatePath != null) {
				if (templatePath.startsWith("class://")) {
					// substring(7) is intentional as we "reuse" the last slash
					templatePathLoader = new ClassTemplateLoader(getClass(),
							templatePath.substring(7));
				} else if (templatePath.startsWith("file://")) {
					templatePathLoader = new FileTemplateLoader(new File(
							templatePath.substring(7)));
				} else {
					return new MultiTemplateLoader(new TemplateLoader[] {
							new WebappTemplateLoader(servletContext,
									templatePath),
							new StrutsClassTemplateLoader() });
				}

			}
		} catch (IOException e) {
			LOG.error("Invalid template path specified: " + e.getMessage(), e);
		}

		// presume that most apps will require the class and webapp template
		// loader
		// if people wish to
		return templatePathLoader != null ? new MultiTemplateLoader(
				new TemplateLoader[] { templatePathLoader,
						new WebappTemplateLoader(servletContext),
						new StrutsClassTemplateLoader() })
				: new MultiTemplateLoader(new TemplateLoader[] {
						new WebappTemplateLoader(servletContext),
						new StrutsClassTemplateLoader() });
	}

	public Map<String, Object> getFreemarkerVariables() {
		return freemarkerVariables;
	}

	public void setFreemarkerVariables(Map<String, Object> freemarkerVariables) {
		this.freemarkerVariables = freemarkerVariables;
	}

	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}

	public String getTemplatePath() {
		return templatePath;
	}
}
