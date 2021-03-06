package kea.gruppe5.project.repository;
//JLJ & MM
import java.sql.*;
import java.util.ArrayList;

import kea.gruppe5.project.models.Project;
import kea.gruppe5.project.utility.DatabaseConnectionManager;

public class ProjectRepository {


    private static ArrayList<Project> projectRepository = new ArrayList<>();

    public static void loadProjects() {
        Connection connection = DatabaseConnectionManager.getConnection();
        String insstr = "SELECT * FROM projects";
        PreparedStatement preparedStatement;
        int results = 0;
        try {
            preparedStatement = connection.prepareStatement(insstr);
     
            ResultSet column =  preparedStatement.executeQuery();
            while(column.next()) {
                Project p = new Project(column.getInt("personnelNumber"), 0, column.getString("name"), column.getString("description"), null, null, column.getInt("projectID"));
                p.setDeadline(column.getString("deadline"));
                projectRepository.add(p);
                results++;
            }

            System.out.println("Fetched " + results + "projects");

        } catch (SQLException err) {
            System.out.println("Something went wrong:" + err.getMessage());
        }


    }

    public static ArrayList<Project> getProjectsByUUID(String uuid) {
        ArrayList<Project> result = new ArrayList<>();

        for (Project project : projectRepository) {
            if (project.getPersonnelNumber() == Integer.parseInt(uuid)) {
                result.add(project);
            }
        }
        System.out.println("User has " + result.size() + " projects");
        return result;
    }

    public static Project getProjectById(String projectid) {
        for (Project project : projectRepository) {
            if (Integer.parseInt(projectid) == project.getId()) {
                System.out.println("Searching for project " + projectid + " found!");
                return project;
            }
        }
        return null;
    }

    public static int createProject(String name, String description, int personnelNumber, String deadline) {
        Connection connection = DatabaseConnectionManager.getConnection();
        String insstr = "INSERT INTO projects(name, description, personnelNumber, deadline) values (?,?,?,?)";
        PreparedStatement preparedStatement;
        int result = 0;
        try {
            preparedStatement = connection.prepareStatement(insstr, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name.replace("[", "").replace("]", ""));
            preparedStatement.setString(2, description.replace("[", "").replace("]", ""));
            preparedStatement.setInt(3, personnelNumber);
            preparedStatement.setString(4, deadline);
            preparedStatement.executeUpdate();
            ResultSet column = preparedStatement.getGeneratedKeys();
            System.out.println(preparedStatement);
            if (column.next()) {
                result = column.getInt(1);
                System.out.println("Created column " + result);
            }

        } catch (SQLException err) {
            System.out.println("Something went wrong:" + err.getMessage());
            return -1;
        }
        
        System.out.println("Project created successfully");
        Project project = new Project(personnelNumber, 0, name,description, null, null, result);
        project.setDeadline(deadline);
        projectRepository.add(project);
        return result;

    }
    

    public static boolean updateProject(String name, String description, int id, String deadline) {
        Connection connection = DatabaseConnectionManager.getConnection();
        String insstr = "UPDATE projects set name = ?, description = ?, deadline = ? WHERE projectID = ?";
        PreparedStatement preparedStatement;
        try {
            preparedStatement = connection.prepareStatement(insstr, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name.replace("[", "").replace("]", ""));
            preparedStatement.setString(2, description.replace("[", "").replace("]", ""));
            preparedStatement.setInt(3, id);
            preparedStatement.setString(4, deadline);
            preparedStatement.executeUpdate();
            System.out.println("Task updated in database");
 
        } catch (SQLException err) {
            System.out.println("Something went wrong:" + err.getMessage());
            return false;
        }
        for (Project project : projectRepository) {
            if (project.getId() == id) {
                project.setName(name);
                project.setDescription(description);
                project.setDeadline(deadline);
                System.out.println("Project successfully updated - id: " + project.getId());
                return true;

            }
        }
        return false;
    }



    public static void calculateTime(int projectId, double time, double daysLeft, double hoursADay) {
        for (Project project : projectRepository) {
            if (project.getId() == projectId) {
                project.setTotalTime(time);
                project.setDaysLeft(daysLeft);
                project.setHoursADay(hoursADay);
                System.out.println("New project time: " + project.getTotalTime());
            }
        }
    }
    
}

