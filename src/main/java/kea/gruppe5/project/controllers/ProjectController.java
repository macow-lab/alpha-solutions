package kea.gruppe5.project.controllers;
//JLJ & MM

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;

import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kea.gruppe5.project.service.ProjectService;
import kea.gruppe5.project.service.SubProjectService;
import kea.gruppe5.project.service.SubtaskService;
import kea.gruppe5.project.service.TaskService;

import java.text.ParseException;

@Controller
@RequestMapping("myprojects")
public class ProjectController {

    @GetMapping("/")
    public String myprojects(Model model, HttpSession session) {
        if (session.getAttribute("email") == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("projects", ProjectService.getProjectsByUUID
        (String.valueOf(session.getAttribute("personnelNumber"))));

        return "project/myprojects";
    }

    @GetMapping("/calculateTime") 
    public String calculateTime(@RequestParam(value = "id", required = true) String id, RedirectAttributes redirectAttrs) throws ParseException {
        ProjectService.calculateTime(Integer.parseInt(id));
        redirectAttrs.addAttribute("id", id);
        return "redirect:/myprojects/projects?id={id}";
    }

    /*
            PROJECTS
    */
    @GetMapping("projects") 
    public String viewProject(Model model, @RequestParam(value = "id", required = true) String id) {
        // Tilføj modelattribute med Subprojects
        model.addAttribute("subprojects", SubProjectService.getSubprojectsByParentId(Integer.parseInt(id)));
        model.addAttribute("project", ProjectService.getProjectById(id));
        System.out.println("ID: " + id);
        return "project/viewproject";
    }


    @GetMapping("updateproject")
    public String viewUpdateProject(Model model, @RequestParam(value = "id", required = true) String id) {
        System.out.println(ProjectService.getProjectById(id));
        model.addAttribute("project", ProjectService.getProjectById(id));
        return "project/updateproject";
    }

    @PostMapping("updateproject")
    public String updateProject(@RequestParam MultiValueMap body, RedirectAttributes redirectAttrs,  @RequestParam(value = "id", required = true) String id) {
        String name = String.valueOf(body.get("name")).replace("[","").replace("]","");
        String description = String.valueOf(body.get("description")).replace("[","").replace("]","");
        String deadline = String.valueOf(body.get("deadline")).replace("[","").replace("]","");

        boolean updated = ProjectService.updateProject(name, description, Integer.parseInt(id), deadline);
        
        if (updated) {
            redirectAttrs.addAttribute("id", id);
            return "redirect:/myprojects/projects?id={id}";
        }

        return "redirect:/myprojects/updateproject?status=fail";
    }
    

    @GetMapping("createproject") 
    public String createProjectView() {
        return "project/createproject";
    }

    @PostMapping("createproject")
    public String createProject(@RequestParam MultiValueMap body, RedirectAttributes redirectAttrs, HttpSession session) {
        String name = String.valueOf(body.get("name")).replace("[","").replace("]","");
        String description = String.valueOf(body.get("description")).replace("[","").replace("]","");
        String deadline = String.valueOf(body.get("deadline")).replace("[","").replace("]","");
        int personnelNumber = (int) session.getAttribute("personnelNumber");



        // Hvis en model bliver returneret fra ProjectService, bliver den brugt til redirect
        int createdProjectId = ProjectService.createProject(name, description, personnelNumber, deadline);

        if (createdProjectId >= 0) {
            redirectAttrs.addAttribute("id", createdProjectId);
            return "redirect:/myprojects/projects?id={id}";
        }
        // Handle fail aka hvis createdProjectId er -1
        return "redirect:/myprojects/createproject?status=fail";
    }

        /*
            SUBPROJECTS
         */

    @GetMapping("subproject") 
    public String viewSubproject(Model model, @RequestParam(value = "id", required = true) String id) {
        // Used for viewing a subproject as a 'parent', with associated tasks in cards
        model.addAttribute("subproject", SubProjectService.getSubProjectById(Integer.parseInt(id)));
        model.addAttribute("tasks", TaskService.getTasksByParentId(Integer.parseInt(id)));
        
        return "project/viewsubproject";
    }

    @GetMapping("createsubproject")
    public String viewCreateSubProject(@RequestParam(value = "id", required = true) String id) {
        return "project/createsubproject";
    }

    @PostMapping("createsubproject") 
    public String createSubproject(@RequestParam MultiValueMap body, RedirectAttributes redirectAttrs,  @RequestParam(value = "id", required = true) String id) {
        // Det ID der bliver modtaget er ID for ejeren. I dette tilfælde hvilket projekt det hører under
        
        String name = String.valueOf(body.get("name")).replace("[","").replace("]","");
        String description = String.valueOf(body.get("description")).replace("[","").replace("]","");
        
        int creationResult = SubProjectService.createSubproject(name, description, id);

        if (creationResult >= 0) {
            redirectAttrs.addAttribute("id", creationResult);
            return "redirect:/myprojects/subproject?id={id}";
        }
        
        return "redirect:/myprojects/createsubproject?status=fail";
    }

    @GetMapping("updatesubproject")
    public String viewUpdateSubproject(Model model, @RequestParam(value = "id", required = true) String id) {
        model.addAttribute("subproject", SubProjectService.getSubProjectById(Integer.parseInt(id)));
        return "project/updatesubproject";
    }

    @PostMapping("updatesubproject")
    public String updateSubproject(@RequestParam MultiValueMap body, RedirectAttributes redirectAttrs,  @RequestParam(value = "id", required = true) String id) {
        String name = String.valueOf(body.get("name")).replace("[","").replace("]","");
        String description = String.valueOf(body.get("description")).replace("[","").replace("]","");

        boolean updated = SubProjectService.updateSubProject(name, description, Integer.parseInt(id));

        if (updated) {
            redirectAttrs.addAttribute("id", id);
            return "redirect:/myprojects/subproject?id={id}";
        }

        return "redirect:/myprojects/updatesubproject?status=fail";
    }


    /*
            TASKS
    */

    @GetMapping("tasks") 
    public String viewTasks(Model model, @RequestParam(value = "id", required = true) String id) {
        model.addAttribute("task", TaskService.getTaskById(Integer.parseInt(id)));
        model.addAttribute("subtasks", SubtaskService.getSubtasksByParentId(Integer.parseInt(id)));
        return "project/viewtask";
    }

    @GetMapping("createtask") 
    public String createTask(@RequestParam(value = "id", required = true) String id) {

        return "project/createtask";
    }

    @PostMapping("createtask") 
    public String createtask(@RequestParam MultiValueMap body, RedirectAttributes redirectAttrs,  @RequestParam(value = "id", required = true) String id) {
        // Det ID der bliver modtaget er ID for ejeren. I dette tilfælde hvilket projekt det hører under
        
        String name = String.valueOf(body.get("name")).replace("[","").replace("]","");
        String description = String.valueOf(body.get("description")).replace("[","").replace("]","");
        
        int creationResult = TaskService.createTask(name, description, id);

        if (creationResult >= 0) {
            redirectAttrs.addAttribute("id", creationResult);
            return "redirect:/myprojects/tasks?id={id}";
        }
        
        return "redirect:/myprojects/createtask?status=fail";
    }

    @GetMapping("updatetask")
    public String viewUpdateTask(Model model, @RequestParam(value = "id", required = true) String id) {
        model.addAttribute("task", TaskService.getTaskById(Integer.parseInt(id)));
        return "project/updatetask";
    }

    @PostMapping("updatetask")
    public String updateTask(@RequestParam MultiValueMap body, RedirectAttributes redirectAttrs,  @RequestParam(value = "id", required = true) String id) {
        String name = String.valueOf(body.get("name")).replace("[","").replace("]","");
        String description = String.valueOf(body.get("description")).replace("[","").replace("]","");

        boolean updated = TaskService.updateTask(name, description, Integer.parseInt(id));

        if (updated) {
            redirectAttrs.addAttribute("id", id);
            return "project/myprojects/tasks?id={id}";
        }

        return "redirect:/myprojects/updatesubproject?status=fail";
    }

        /*
            SUBTASKS
         */

    @GetMapping("viewsubtasks") 
    public String viewSubtasks(Model model, @RequestParam(value = "id", required = true) String id) {
        
        return "root";
    }

    @GetMapping("createsubtask") 
    public String createSubtaskView(@RequestParam(value = "id", required = true) String id) {


        return "project/createsubtask";
    }

    @PostMapping("createsubtask")
    public String createSubtask(@RequestParam MultiValueMap body, RedirectAttributes redirectAttrs, @RequestParam(value = "id", required = true) String taskId) {
        // Retrieving POST body values
        String name = String.valueOf(body.get("name")).replace("[","").replace("]","");
        String description = String.valueOf(body.get("description")).replace("[","").replace("]","");
        double time = Double.valueOf(String.valueOf(body.get("time")).replace("[","").replace("]",""));

        // If object is successfully created, returns value with ID - else -1
        int creationResult = SubtaskService.createSubtask(name, description, time, taskId);

        if(creationResult >= 0) {
            redirectAttrs.addAttribute("id", taskId);
            return "redirect:/myprojects/tasks?id={id}";
            
        }

        return "redirect:/myprojects/tasks?status=fail";

    }

    @GetMapping("updatesubtask")
    public String updateSubtaskView(Model model, @RequestParam(value = "id", required = true) String id) {
        
        model.addAttribute("subtask",SubtaskService.getSubtaskById(Integer.parseInt(id)));
        return "project/updatesubtask";
    }

    @PostMapping("updatesubtask")
    public String updateSubtask(@RequestParam MultiValueMap body, RedirectAttributes redirectAttrs, @RequestParam(value = "id", required = true) String id) {
        String name = String.valueOf(body.get("name")).replace("[","").replace("]","");
        String description = String.valueOf(body.get("description")).replace("[","").replace("]","");
        double time = Double.valueOf(String.valueOf(body.get("time")).replace("[","").replace("]",""));

        // If object is successfully created, returns value with ID - else -1
        int taskID = SubtaskService.updateSubtask(name, description, time, Integer.parseInt(id));

        if(taskID >= 0) {
            redirectAttrs.addAttribute("id", taskID);
            return "redirect:/myprojects/tasks?id={id}";
        }

        return "redirect:/myprojects/tasks?status=fail";
    }

}
