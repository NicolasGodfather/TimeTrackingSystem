package com.asinovich.controllers;

import com.asinovich.controllers.forms.ProjectForm;
import com.asinovich.controllers.validator.ProjectFormValidator;
import com.asinovich.dto.ProjectDTO;
import com.asinovich.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;

/**
 * TODO : still not finish
 *
 * @author Nicolas Asinovich.
 */
@Controller
public class ProjectController {

    private static final String All_PROJECT = "allProject";
    private static final String SAVE_PROJECT = "saveProject";
    private static final String ERROR_PAGE = "errorPage";

    @Autowired
    private ProjectFormValidator projectFormValidator;
    @Autowired
    private ProjectService projectService;

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(projectFormValidator);
    }

    @RequestMapping (value = "all/project", method = RequestMethod.GET)
    public String showAllProject(ModelMap modelMap) {
        modelMap.addAttribute("projects", projectService.getAll());
        return All_PROJECT;
    }

    @RequestMapping(value = "/add/project", method = RequestMethod.GET)
    public String showPageAddProject(ModelMap modelMap) {
        modelMap.addAttribute("projectForm", new ProjectForm());
        return SAVE_PROJECT;
    }

    @RequestMapping(value = "/save/project/{id}", method = RequestMethod.GET)
    public String showPageEditProject(@PathVariable (value = "id") Long id, ModelMap modelMap) {
        try {
            ProjectDTO projectDTO = projectService.getById(id);
            modelMap.addAttribute("projectForm", new ProjectForm(projectDTO));
            return SAVE_PROJECT;
        } catch (NullPointerException e) {
            return ERROR_PAGE;
        }
    }

    @RequestMapping(value = "/save/project", method = RequestMethod.POST)
    public String getSaveProject(@Validated ProjectForm projectForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return SAVE_PROJECT;
        }
        if ("".equals(projectForm.getId())) {
            ProjectDTO projectDTO = getProjectDTO(projectForm);
            projectService.insert(projectDTO);
        } else {
            ProjectDTO projectDTO = getProjectDTO(projectForm);
            projectDTO.setId(projectForm.getId());
            projectService.update(projectDTO);
        }
        return "redirect:/all/project";
    }

    private ProjectDTO getProjectDTO(@Validated ProjectForm projectForm) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectName(projectForm.getProjectName());
        return projectDTO;
    }

    @RequestMapping(value = "/delete/project/{id}", method = RequestMethod.GET)
    public RedirectView deleteProject(@PathVariable(value = "id") Long id) {
        projectService.deleteById(id);
        return new RedirectView("/all/project");
    }

}