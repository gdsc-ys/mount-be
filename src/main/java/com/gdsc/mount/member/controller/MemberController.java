package com.gdsc.mount.member.controller;

import com.gdsc.mount.member.dto.CreateMemberRequest;
import com.gdsc.mount.member.dto.MemberResponse;
import com.gdsc.mount.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // get member
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponse> getMemberById(@PathVariable String memberId) {
        return ResponseEntity.ok().body(memberService.findMemberById(memberId));
    }

    // create member
    @PostMapping("/new")
    public ResponseEntity<String> registerMember(@RequestBody CreateMemberRequest request) {
        String id = memberService.createMember(request.getUsername());
        return ResponseEntity.status(201).body(id);
    }

    // delete member
    @DeleteMapping("/{memberId}")
    public ResponseEntity<String> deleteMember(@PathVariable String memberId) {
        memberService.deleteMemberById(memberId);
        return ResponseEntity.status(204).body("Successfully deleted.");
    }
}