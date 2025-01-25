package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {

	private static final String CATAGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATAGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";
	
	/*
	 * This method inserts a project based on user input to create a insert request
	 */
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
	} // insertProject method

	/*
	 * This method gets a list of projects 
	 */
	public List<Project> fetchAllProjects() {
		String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";

		/*
		 * obtains a connection
		 */
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			/*
			 * transaction
			 */
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				try (ResultSet rs = stmt.executeQuery()) {
					List<Project> projects = new LinkedList<>();

					while (rs.next()) {
						projects.add(extract(rs, Project.class));
					}// while block

					return projects;
				} // try block
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			} // inner try-catch

		} catch (SQLException e) {
			throw new DbException(e);
		} // outer try-catch
	} // fetchAllProject method

	/*
	 * This method gets a project from the list by Id 
	 */
	public Optional<Project> fetchProjectId(Integer projectId) {
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		
		/*
		 * obtains a connection
		 */
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			/*
			 * transaction
			 */
			try {
				Project project = null;

				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					setParameter(stmt, 1, projectId, Integer.class);

					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							project = extract(rs, Project.class);
						} // if statement
					} // try
				} // try

				if (Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
					project.getSteps().addAll(fetchStepsForProject(conn, projectId));
					project.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
				} // if statement

				commitTransaction(conn);

				return Optional.ofNullable(project);

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			} // inner try-catch

		} catch (SQLException e) {
			throw new DbException(e);
		} // outer try-catch
	} // fetchProjectId method

	/*
	 * This method gets the categories for the project that has been selected 
	 */
	private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
		//@formatter:off
		String sql = "" 
			+ "SELECT c.* "
			+ "FROM " + CATAGORY_TABLE + " c "
			+ "JOIN " + PROJECT_CATAGORY_TABLE + " pc USING (category_id) "
			+ "WHERE project_id = ? ";
		//@formatter:on

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();

				while (rs.next()) {
					categories.add(extract(rs, Category.class));
				} // while block

				return categories;
			} // try
		} // try catch
	} // fetchCategoriesForProject method

	/*
	 * This method gets the steps for the project that has been selected 
	 */
	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
		//@formatter:off
		String sql = "" 
			+ "SELECT s.* "
			+ "FROM " + STEP_TABLE + " s "
			+ "JOIN " + PROJECT_TABLE + " p USING (project_id) "
			+ "WHERE project_id = ? ";
		//@formatter:on

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();

				while (rs.next()) {
					steps.add(extract(rs, Step.class));
				}// while block

				return steps;
			} // try
		} // try catch
	} // fetchStepsForProject method

	/*
	 * This method gets the materials for the project that has been selected 
	 */
	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
		//@formatter:off
		String sql = "" 
			+ "SELECT m.* "
			+ "FROM " + MATERIAL_TABLE + " m "
			+ "JOIN " + PROJECT_TABLE + " p USING (project_id) "
			+ "WHERE project_id = ? ";
		//@formatter:on

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			setParameter(stmt, 1, projectId, Integer.class);

			try (ResultSet rs = stmt.executeQuery()) {
				List<Material> materials = new LinkedList<>();

				while (rs.next()) {
					materials.add(extract(rs, Material.class));
				} // while block

				return materials;
			} // try
		} // try catch
	} // fetchMaterialsForProject method

	/*
	 * This method modifies the current projects details on the project table based on user input 
	 */
	public boolean modifyProjectsDetails(Project project) {
		//@formatter:off
		String sql = ""
			+ "UPDATE " + PROJECT_TABLE + " SET "
			+ "project_name = ?, "
			+ "estimated_hours = ?, "
			+ "actual_hours = ?, "
			+ "difficulty = ?, "
			+ "notes = ? "
			+ "WHERE project_id = ?";
		//@formatter:on

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
				setParameter(stmt, 6, project.getProjectId(), Integer.class);

				boolean modified = stmt.executeUpdate() == 1;
				commitTransaction(conn);

				return modified;

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException();
			} // inner try-catch 

		} catch (SQLException e) {
			throw new DbException(e);
		} // outer try-catch
	} // modifyProjectsDetails method

	/*
	 * This method deletes a project based on user input 
	 */
	public boolean deleteProject(Integer projectId) {
		String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";

		/*
		 * obtains a connection
		 */
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			/*
			 * transaction
			 */
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				setParameter(stmt, 1, projectId, Integer.class);

				boolean deleted = stmt.executeUpdate() == 1;

				commitTransaction(conn);
				return deleted;
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			} // inner try-catch
		} catch (SQLException e) {
			throw new DbException(e);
		} // outer try-catch
	} // deleteProject method

	/*
	 * This method resets the tables to a default in the resources 
	 */
	public void executeBatch(List<String> sqlBatch) {
		
		/*
		 * obtains a connection
		 */
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);

			/*
			 * transaction
			 */
			try (Statement stmt = conn.createStatement()) {
				for (String sql : sqlBatch) {
					stmt.addBatch(sql);
				}
				
				stmt.executeBatch();
				
				commitTransaction(conn);
			
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}// inner try-catch
		
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}// outer try-catch

} // class ProjectDao
