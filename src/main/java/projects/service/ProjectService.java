package projects.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

public class ProjectService {
	private static final String SCHEMA_FILE = "projects_schema.sql";
	private static final String DATA_FILE = "project_data.sql";

	private ProjectDao projectDao = new ProjectDao();

	/*
	 * This method redirects to ProjectDao to add a project
	 */
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	} // addProject method
	
	/*
	 * This method redirects to ProjectDao to get all the projects
	 * and it sorts the projects by number
	 */
	public List<Project> fetchAllProjects() {
		// @formatter:off
		return projectDao.fetchAllProjects()
			.stream()
			.sorted((r1, r2) -> r1.getProjectId() - r2.getProjectId())
			.collect(Collectors.toList());
		// @formatter:on
	} // fetchAllProjects method

	/*
	 * This method redirects to ProjectDao to get a project by Id number
	 */
	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectId(projectId).orElseThrow(
				() -> new NoSuchElementException(" Project with project ID=" + projectId + " does not exist."));
	} // fetchProjectById method

	/*
	 * This method redirects to ProjectDao to modify the current working project
	 */
	public void modifyProjectDetails(Project project) {
		if(!projectDao.modifyProjectsDetails(project)) {
			throw new DbException("Project with ID=" + project.getProjectId() + " does not exist.");
		} // if statement
		
	} // modifyProjectDetails method

	/*
	 * This method redirects to ProjectDao to delete a project
	 */
	public void deleteProject(Integer projectId) {
		if(!projectDao.deleteProject(projectId)) {
			throw new DbException("Project with ID=" + projectId + " does not exist.");
		} // if statement
		
	} // deleteProject method

	/*
	 * This method pulls in the SCHEMA and DATA files to create the starting tables
	 */
	public void createAndPopulateTables() {
		loadFromFile(SCHEMA_FILE);
		loadFromFile(DATA_FILE);		
	} // createAndPopulateTables method
	
	/*
	 * This method loads the indicated file and runs it to create the tables 
	 */
	private void loadFromFile(String fileName) {
		String content = readFileContent(fileName);
		List<String> sqlStatements = convertContentToSqlStatements(content);

		projectDao.executeBatch(sqlStatements);
	} // loadFromFile method
	
	/*
	 * This method reads the indicated file
	 */
	private String readFileContent(String fileName) {
		try {
			Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
			return Files.readString(path);
		} catch (Exception e) {
			throw new DbException(e);
		}// try-catch block
	} // readFileContent method
	
	/*
	 * This method converts the starting files into usable sql data
	 */
	private List<String> convertContentToSqlStatements(String content) {
		content = removeComments(content);
		content = replaceWhiteSpaceSequencesWithSingleSpace(content);

		return extractLinesFromContent(content);
	} // convertContentToSqlStatements method
	
	/*
	 * This method removes comments from the files so it can be usable
	 */
	private String removeComments(String content) {
		StringBuilder builder = new StringBuilder(content);
		int commentPos = 0;

		while ((commentPos = builder.indexOf("-- ", commentPos)) != -1) {
			int eolPos = builder.indexOf("\n", commentPos + 1);

			if (eolPos == -1) {
				builder.replace(commentPos, builder.length(), "");
			} else {
				builder.replace(commentPos, eolPos + 1, "");
			} // if statement
		} // while block
		return builder.toString();
	} // removeComments method
	
	/*
	 * This method removes whitespace that can cause problems with the sql
	 */
	private String replaceWhiteSpaceSequencesWithSingleSpace(String content) {
		return content.replaceAll("\\s+", " ");
	} // replaceWhiteSpaceSequencesWithSingleSpace method
	
	/*
	 * This method converts the modified files into usable sql
	 */
	private List<String> extractLinesFromContent(String content) {
		List<String> lines = new LinkedList<>();

		while (!content.isEmpty()) {
			int semicolon = content.indexOf(";");

			if (semicolon == -1) {
				if (!content.isBlank()) {
					lines.add(content);
				} // if statement
				content = "";
			} else {
				lines.add(content.substring(0, semicolon).trim());
				content = content.substring(semicolon + 1);
			} // if statement
		} // while block

		return lines;
	} // extractLinesFromContent method

} //class ProjectService 
