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
	 * This method redirects to ProjectDao
	 */
	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

	public List<Project> fetchAllProjects() {
		// @formatter:off
		return projectDao.fetchAllProjects()
			.stream()
			.sorted((r1, r2) -> r1.getProjectId() - r2.getProjectId())
			.collect(Collectors.toList());
		// @formatter:on
	}

	public Project fetchProjectById(Integer projectId) {
		return projectDao.fetchProjectId(projectId).orElseThrow(
				() -> new NoSuchElementException(" Project with project ID=" + projectId + " does not exist."));
	}

	public void modifyProjectDetails(Project project) {
		if(!projectDao.modifyProjectsDetails(project)) {
			throw new DbException("Project with ID=" + project.getProjectId() + " does not exist.");
		}
		
	}

	public void deleteProject(Integer projectId) {
		if(!projectDao.deleteProject(projectId)) {
			throw new DbException("Project with ID=" + projectId + " does not exist.");
		}
		
	}

	public void createAndPopulateTables() {
		loadFromFile(SCHEMA_FILE);
		loadFromFile(DATA_FILE);		
	}
	
	private void loadFromFile(String fileName) {
		String content = readFileContent(fileName);
		List<String> sqlStatements = convertContentToSqlStatements(content);

		projectDao.executeBatch(sqlStatements);
	}
	
	private String readFileContent(String fileName) {
		try {
			Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
			return Files.readString(path);
		} catch (Exception e) {
			throw new DbException(e);
		}
	}
	
	private List<String> convertContentToSqlStatements(String content) {
		content = removeComments(content);
		content = replaceWhiteSpaceSewuencesWithSingleSpace(content);

		return extractLinesFromContent(content);
	}
	
	private String removeComments(String content) {
		StringBuilder builder = new StringBuilder(content);
		int commentPos = 0;

		while ((commentPos = builder.indexOf("-- ", commentPos)) != -1) {
			int eolPos = builder.indexOf("\n", commentPos + 1);

			if (eolPos == -1) {
				builder.replace(commentPos, builder.length(), "");
			} else {
				builder.replace(commentPos, eolPos + 1, "");
			}
		}
		return builder.toString();
	}
	
	private String replaceWhiteSpaceSewuencesWithSingleSpace(String content) {
		return content.replaceAll("\\s+", " ");
	}
	
	private List<String> extractLinesFromContent(String content) {
		List<String> lines = new LinkedList<>();

		while (!content.isEmpty()) {
			int semicolon = content.indexOf(";");

			if (semicolon == -1) {
				if (!content.isBlank()) {
					lines.add(content);
				}
				content = "";
			} else {
				lines.add(content.substring(0, semicolon).trim());
				content = content.substring(semicolon + 1);
			}
		}

		return lines;
	}

}
