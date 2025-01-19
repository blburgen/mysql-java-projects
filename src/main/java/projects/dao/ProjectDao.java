package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {

	private static final String CATAGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATAGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	public Project insertProject(Project project) {
		// @formatter:off
		String sql = ""
			+ "INSERT INTO " + PROJECT_TABLE + " "
			+ "(project_name, estimated_hours, actual_hours, difficulty, notes) "
			+ "VALUES "
			+ "(?, ?, ?, ?, ?)";
		// @formatter:on

		/*
		 * obtains a connection
		 */
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			/*
			 * transaction
			 */
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);

				stmt.executeUpdate();

				/*
				 * gets project Id then commits the transaction
				 */
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);

				project.setProjectId(projectId);
				return project;

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			} // inner try-catch
		} catch (SQLException e) {
			throw new DbException(e);
		} // outer try-catch
	}

	public List<Project> fetchAllProjects() {
		String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";

		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				try (ResultSet rs = stmt.executeQuery()) {
					List<Project> projects = new LinkedList<>();

					while (rs.next()) {
						projects.add(extract(rs, Project.class));
					}

					return projects;
				}
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}

		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

}
