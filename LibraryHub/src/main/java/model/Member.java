package model;

/**
 * Plain data object representing one row of the Members table.
 * Holds no SQL and no business logic — that lives in MemberDAO / MemberService.
 *
 * Matches schema.sql exactly:
 *   member_id INT AUTO_INCREMENT PRIMARY KEY
 *   name      VARCHAR(100) NOT NULL
 *   email     VARCHAR(100) UNIQUE NOT NULL
 *   phone     VARCHAR(20)
 */
public class Member {

    private int memberId;
    private String name;
    private String email;
    private String phone;

    /**
     * Full constructor — used when a member is loaded FROM the database,
     * where the ID is already known.
     */
    public Member(int memberId, String name, String email, String phone) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    /**
     * No-ID constructor — used when registering a NEW member,
     * since MySQL assigns member_id automatically (AUTO_INCREMENT).
     */
    public Member(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // --- Getters and setters ---

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Member{" +
                "memberId=" + memberId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}