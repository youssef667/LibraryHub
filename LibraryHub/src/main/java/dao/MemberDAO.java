package dao;

import model.Member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the Members table.
 * Contains only SQL/JDBC logic — no business rules.
 * Every method opens its own connection (factory-style) and
 * closes it automatically via try-with-resources.
 */
public class MemberDAO {

    /**
     * Inserts a new member into the database.
     * Returns the generated member_id, or -1 if the insert failed
     * (e.g. duplicate email, which violates the UNIQUE constraint).
     * Friendly "email already exists" handling belongs in MemberService,
     * not here — this layer just reports success/failure.
     */
    public int addMember(Member member) {
        String sql = "INSERT INTO Members (name, email, phone) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getPhone());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error adding member: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retrieves a single member by their ID.
     * Returns null if no member with that ID exists.
     */
    public Member getMemberById(int memberId) {
        String sql = "SELECT * FROM Members WHERE member_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMember(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching member: " + e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves every member in the database.
     */
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM Members";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                members.add(mapRowToMember(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching members: " + e.getMessage());
        }
        return members;
    }

    /**
     * Searches members whose name contains the given keyword
     * (case-insensitive partial match).
     */
    public List<Member> searchMembersByName(String keyword) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM Members WHERE name LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(mapRowToMember(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching members: " + e.getMessage());
        }
        return members;
    }

    /**
     * Updates a member's name, email, and phone.
     * Returns true if a row was actually updated.
     */
    public boolean updateMember(Member member) {
        String sql = "UPDATE Members SET name = ?, email = ?, phone = ? WHERE member_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getPhone());
            stmt.setInt(4, member.getMemberId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating member: " + e.getMessage());
        }
        return false;
    }

    /**
     * Deletes a member by their ID.
     * Returns true if a row was actually deleted.
     *
     * Note: Members referenced by a row in Loans cannot be deleted
     * due to the foreign key constraint — MySQL will throw a SQLException.
     * That case is caught here and simply reported as a failed delete;
     * MemberService can later give a friendlier message.
     */
    public boolean deleteMember(int memberId) {
        String sql = "DELETE FROM Members WHERE member_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting member: " + e.getMessage());
        }
        return false;
    }

    /**
     * Maps the current row of a ResultSet to a Member object.
     * Kept as one private helper so every read method builds
     * Member objects the same way.
     */
    private Member mapRowToMember(ResultSet rs) throws SQLException {
        return new Member(
                rs.getInt("member_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("phone")
        );
    }
}