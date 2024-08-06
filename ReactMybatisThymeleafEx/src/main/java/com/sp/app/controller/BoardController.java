package com.sp.app.controller;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sp.app.domain.Board;
import com.sp.app.service.BoardService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/*
  - @CrossOrigin
    : CORS(Cross-origin resource sharing)란?
      웹 페이지의 제한된 자원을 외부 도메인에서 접근을 허용해주는 메커니즘
    : CORS를 스프링을 통해 설정할 수 있는 기능
    : @CrossOrigin 어노테이션을 붙여주면 기본적으로 
      '모든 도메인, 모든 요청방식' 에 대해 허용 한다는 뜻이다.
    : 속성 중 origins는 허용할 도메인을 나타낸다. 
      복수개일 경우 콤마로 구분하여 넣어주면 된다.
      @CrossOrigin(origins = "http://domain1.com, http://domain2.com")
      * 는 모두 허용 
    : 메소드에서도 가능
*/
@CrossOrigin(origins = "http://localhost:3000") // 지정 사이트에서 AJAX 요청 허용
@RestController
@RequiredArgsConstructor
public class BoardController {
	private final BoardService service;

	@GetMapping("/board")
	public Map<String, Object> list(@RequestParam(name = "pageNo", defaultValue = "1") int current_page,
			@RequestParam(value = "pageSize", defaultValue = "10") int size,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd) throws Exception {

		Map<String, Object> model = new HashMap<>();
		
		try {
			int total_page = 0;
			int dataCount = 0;

			kwd = URLDecoder.decode(kwd, "utf-8");

			// 전체 페이지 수
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("schType", schType);
			map.put("kwd", kwd);

			dataCount = service.dataCount(map);
			if (dataCount != 0) {
				total_page = dataCount / size + (dataCount % size > 0 ? 1 : 0);
			}

			// 다른 사람이 자료를 삭제하여 전체 페이지수가 변화 된 경우
			if (total_page < current_page) {
				current_page = total_page;
			}

			// 리스트에 출력할 데이터를 가져오기
			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;

			map.put("offset", offset);
			map.put("size", size);

			List<Board> list = service.listBoard(map);
			
			model.put("list", list);
			model.put("pageNo", current_page);
			model.put("total_page", total_page);
			model.put("dataCount", dataCount);
			model.put("size", size);

			model.put("schType", schType);
			model.put("kwd", kwd);
			
			model.put("state", "true");
		} catch (Exception e) {
			model.put("state", "false");
		}

		return model;
	}

	// JSON 으로 넘어옴
	@PostMapping("/board")
	public Map<String, Object> writeSubmit(@RequestBody Board dto, 
			HttpServletRequest req) throws Exception {
		Map<String, Object> model = new HashMap<>();
		
		String state = "true";
		try {
			dto.setIpAddr(req.getRemoteAddr());
			service.insertBoard(dto);
		} catch (Exception e) {
			state = "false";
		}
		
		model.put("state", state);

		return model;
	}

	@GetMapping("/board/{num}")
	public Map<String, Object> article(@PathVariable(name = "num") long num,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd) throws Exception {
		
		Map<String, Object> model = new HashMap<>();
		
		try {
			kwd = URLDecoder.decode(kwd, "utf-8");

			// 조회수 증가 및 해당 레코드 가져 오기
			service.updateHitCount(num);

			Board dto = service.findById(num);
			if (dto == null) {
				model.put("state", "false");
				return model;
			}

			// 이전 글, 다음 글
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("schType", schType);
			map.put("kwd", kwd);
			map.put("num", num);

			Board prevDto = service.findByPrev(map);
			Board nextDto = service.findByNext(map);

			model.put("dto", dto);
			model.put("prevDto", prevDto);
			model.put("nextDto", nextDto);

			model.put("state", "true");
		} catch (Exception e) {
			model.put("state", "false");
		}
		
		return model;
	}
	
	@GetMapping("/board/read/{num}")
	public Map<String, Object> read(@PathVariable(name = "num") long num) throws Exception {
		Map<String, Object> model = new HashMap<>();
		
		try {
			Board dto = service.findById(num);
			if (dto == null) {
				model.put("state", "false");
				return model;
			}

			model.put("dto", dto);

			model.put("state", "true");
		} catch (Exception e) {
			model.put("state", "false");
		}
		
		return model;
	}

	@DeleteMapping("/board/{num}")
	public Map<String, Object> delete(@PathVariable(name = "num") long num) throws Exception {

		Map<String, Object> model = new HashMap<>();
		
		try {
			service.deleteBoard(num);
			model.put("state", "true");
		} catch (Exception e) {
			model.put("state", "false");
		}

		return model;
	}

	// @PatchMapping : 일부분 수정
	// @PutMapping : 모두 수정
	@PatchMapping("/board")
	public Map<String, Object> updateSubmit(@RequestBody Board dto) throws Exception {
		Map<String, Object> model = new HashMap<>();
		
		try {
			service.updateBoard(dto);
			model.put("state", "true");
		} catch (Exception e) {
			model.put("state", "false");
		}

		return model;
	}
}
