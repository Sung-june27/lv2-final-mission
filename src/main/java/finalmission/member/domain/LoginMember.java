package finalmission.member.domain;

public record LoginMember(
        Long id,
        String name,
        String email,
        Role role
) {
}

