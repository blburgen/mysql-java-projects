package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {

	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;

	// @formatter:off
	// This is the list of menu options
	private List<String> operations = List.of(
		"1) Add a project",
		"2) List projects",
		"3) Select a project"
	);
	// @formatter:on

	/*
	 * This is the main method.  This is the access point
	 */
	public static void main(String[] args) {
		new ProjectsApp().processUserSelection();

	} // main

	/*
	 * This method compares looks at the user's menu choice and calls needed methods.
	 */
	private void processUserSelection() {
		boolean done = false;

		while (!done) {
			try {
				int selection = getUserSelection();
				// @formatter:off
				switch (selection) {
					case -1:
						done = exitMenu();
						break;
						
					case 1:
						createProject();
						break;
						
					case 2:
						listProjects();
						break;
						
					case 3:
						selectProject();
						break;
						
					default:
						System.out.println("\n" + selection + " is not a valid selection. Try again.");
						break;
				} // @formatter:on
				// switch statement
			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			} // try catch statement
		} // While loop

	} // processUserSelection method

	private void selectProject() {
		listProjects();
		
		Integer projectId = getIntInput("Enter a project ID to select a project");
		
		curProject = null;
		
		curProject = projectService.fetchProjectById(projectId);
		
		if(curProject == null) {
			System.out.println("Invalid project ID selected.");
		}		
	}

	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project -> System.out.println("  " + project.getProjectId() + ": " + project.getProjectName()));
	}

	/*
	 * This method get the new project information from the user and then calls ProjectService to add it to the tables
	 */
	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");

		Project project = new Project();

		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created poject: " + dbProject);
	} // createProjects method

	/*
	 * This method takes a string and changes it to a 2 decimal number
	 */
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		} // if statement

		try {
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a vaild decimal number.");
		} // try catch statement
	} // getDecimalInput method

	/*
	 * The method takes the users menu input and returns it to the processUserSelection method
	 */
	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput("Enter a menu selection");

		return Objects.isNull(input) ? -1 : input;
	} // getUserSelection method

	/*
	 * This method prints out the menu
	 */
	private void printOperations() {
		System.out.println("\nThese are the available sections. Press the Enter key to quit:");

		operations.forEach(line -> System.out.println("  " + line));
		
		if(Objects.isNull(curProject)) {
			System.out.println("\n You are not working with a project.");
		} else {
			System.out.println("\nYou are working with project: " + curProject);
		}

	} // printOperations method

	/*
	 * This method takes a string and changes it to a integer
	 */
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		} // if statement

		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a vaild number.");
		} // try catch statement

	} // getIntInput method

	/*
	 * This method takes a string method and returns it trimmed
	 */
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();

		return input.isBlank() ? null : input.trim();
	} // getStringInput method

	/*
	 * This method exits the program
	 */
	private boolean exitMenu() {
		System.out.println("Exiting the menu.");
		return true;
	} // exitMenu method

} // class ProjectsApp
