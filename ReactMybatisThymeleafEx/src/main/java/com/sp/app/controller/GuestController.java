package com.sp.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sp.app.domain.Guest;
import com.sp.app.service.GuestService;

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
*/
@CrossOrigin(origins = "http://localhost:3000") // 지정 사이트에서 AJAX 요청 허용
@RestController
@RequiredArgsConstructor
@RequestMapping("/guest/*")
public class GuestController {
	private final GuestService service;

	// JSON으로 넘어옴
	@PostMapping("insert")
	public Map<String, Object> insertSubmit(@RequestBody Guest dto) throws Exception {
		Map<String, Object> model = new HashMap<>();
		
		try {
			service.insertGuest(dto);
			model.put("state", "true");
		} catch (Exception e) {
			model.put("state", "false");
		}

		return model;
	}

	// AJAX - JSON
	@GetMapping(value = "list")
	public Map<String, Object> list(
			@RequestParam(value = "pageNo", defaultValue = "1") int current_page,
			@RequestParam(value = "pageSize", defaultValue = "5") int size) throws Exception {
		Map<String, Object> model = new HashMap<>();
		
		try {
			int dataCount = service.dataCount();
			int total_page = 0;
			if (dataCount != 0) {
				total_page = dataCount / size + (dataCount % size > 0 ? 1 : 0);
			}		

			if (current_page > total_page) {
				current_page = total_page;
			}

			Map<String, Object> map = new HashMap<String, Object>();

			int offset = (current_page - 1) * size;
			if(offset < 0) offset = 0;

			map.put("offset", offset);
			map.put("size", size);

			List<Guest> list = service.listGuest(map);

			model.put("list", list);
			model.put("dataCount", dataCount);
			model.put("pageNo", current_page);
			model.put("total_page", total_page);
			
			model.put("state", "true");
		} catch (Exception e) {
			model.put("state", "false");
		}

		return model;
	}

	// AJAX - JSON
	@PostMapping("delete/{num}")
	public Map<String, Object> delete(@PathVariable(name = "num") long num) throws Exception {
		Map<String, Object> model = new HashMap<>();
		
		try {
			service.deleteGuest(num);
			
			model.put("state", "true");
		} catch (Exception e) {
			model.put("state", "false");
		}
		
		return model;
	}
}
