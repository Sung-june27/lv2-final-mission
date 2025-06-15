package finalmission.member.service;

import finalmission.global.error.exception.BadRequestException;
import finalmission.global.util.JwtUtil;
import finalmission.member.domain.LoginMember;
import finalmission.member.domain.Member;
import finalmission.member.dto.request.LoginRequest;
import finalmission.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public String login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("이메일 또는 비밀번호가 일치하지 않습니다."));

        validatePasswordCorrect(request, member);

        LoginMember loginMember = new LoginMember(member.getId(), member.getName(), member.getEmail(), member.getRole());
        return jwtUtil.createToken(loginMember);
    }

    private void validatePasswordCorrect(LoginRequest request, Member member) {
        if (!member.matchesPassword(request.password())) {
            throw new BadRequestException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
    }
}
