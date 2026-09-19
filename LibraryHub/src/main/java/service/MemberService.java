package service;

import dao.MemberDAO;
import model.Member;

import java.util.List;

/**
 * Business logic layer for members.
 * MemberDAO answers "can this be written to the Members table."
 * MemberService answers "should this happen at all" — input validation
 * and friendlier handling of DB-level constraint failures live here.
 */
public class MemberService {

    private final MemberDAO memberDAO;

    public MemberService() {
        this.memberDAO = new MemberDAO();
    }

    /**
     * Validates input, then registers a new member.
     * Returns the generated member_id, or -1 if validation failed
     * OR the insert itself failed (most commonly a duplicate email,
     * since email is UNIQUE NOT NULL in the schema).
     * We don't query the DB first to check "does this email already
     * exist" — that would be a second round-trip and a race condition
     * waiting to happen. Instead we let the UNIQUE constraint do its
     * job and just report a friendlier message when the insert fails.
     */
    public int addMember(String name, String email, String phone) {
        if (isBlank(name) || isBlank(email)) {
            System.out.println("Name and email cannot be empty.");
            return -1;
        }
        if (!email.contains("@")) {
            System.out.println("Email format looks invalid.");
            return -1;
        }

        Member member = new Member(name.trim(), email.trim(), isBlank(phone) ? null : phone.trim());
        int newId = memberDAO.addMember(member);

        if (newId == -1) {
            System.out.println("Could not register member — email may already be in use.");
        }
        return newId;
    }

    /**
     * Returns every registered member. No rules needed to just list them.
     */
    public List<Member> getAllMembers() {
        return memberDAO.getAllMembers();
    }

    /**
     * Returns a single member by ID, or null if not found.
     */
    public Member getMemberById(int memberId) {
        return memberDAO.getMemberById(memberId);
    }

    /**
     * Validates the search keyword, then searches by name.
     */
    public List<Member> searchMembers(String keyword) {
        if (isBlank(keyword)) {
            System.out.println("Search keyword cannot be empty.");
            return List.of();
        }
        return memberDAO.searchMembersByName(keyword.trim());
    }

    /**
     * Validates input, then updates an existing member's name/email/phone.
     */
    public boolean updateMember(Member member) {
        if (member == null || isBlank(member.getName()) || isBlank(member.getEmail())) {
            System.out.println("Name and email cannot be empty.");
            return false;
        }
        if (!member.getEmail().contains("@")) {
            System.out.println("Email format looks invalid.");
            return false;
        }
        return memberDAO.updateMember(member);
    }

    /**
     * Deletes a member by ID.
     * Note: this does NOT check whether the member has an active loan.
     * MemberService only knows about Members — it has no reason to know
     * Loans exists. If the member has any loan history at all, the
     * foreign key in Loans will make the DELETE fail anyway (MemberDAO
     * already catches that SQLException and returns false here);
     * a friendlier "this member has borrowing history" message belongs
     * one layer up, where both Members and Loans are already in view.
     */
    public boolean deleteMember(int memberId) {
        return memberDAO.deleteMember(memberId);
    }

    /**
     * Small shared helper: treats null and whitespace-only strings as blank.
     */
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}