package com.sp.app.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sp.app.domain.Guest;
import com.sp.app.mapper.GuestMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuestServiceImpl implements GuestService {
	private final GuestMapper mapper;

	@Override
	public void insertGuest(Guest dto) throws Exception {
		try {
			mapper.insertGuest(dto);
		} catch (Exception e) {
			log.info("GuestService - insertGuest : ", e);
			throw e;
		}
	}

	@Override
	public void deleteGuest(long num) throws Exception {
		try {
			mapper.deleteGuest(num);
		} catch (Exception e) {
			log.info("GuestService - deleteGuest : ", e);
			throw e;
		}
	}
	
	@Override
	public int dataCount() {
		int result = 0;
		try {
			result = mapper.dataCount();
		} catch (Exception e) {
			log.info("GuestService - dataCount : ", e);
		}
		return result;
	}

	@Override
	public List<Guest> listGuest(Map<String, Object> map) {
		List<Guest> list = null;
		try {
			list = mapper.listGuest(map);
		} catch (Exception e) {
			log.info("GuestService - listGuest : ", e);
		}
		return list;
	}

}
