package com.app.ctimetable.service.impl;

import com.app.ctimetable.mapper.CalendarMapper;
import com.app.ctimetable.service.CalendarService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CalendarServiceImpl implements CalendarService {
    private final CalendarMapper calendarMapper;


    @Autowired
    public CalendarServiceImpl(CalendarMapper calendarMapper) {
        this.calendarMapper = calendarMapper;
    }


    @Override
    public List<String> getTerms(int userId) {
        return calendarMapper.getTerms(userId);
    }

    @Override
    public String getCalendar(int userId, String term) {
        return calendarMapper.getCalendar(userId, term);
    }

    @Override
    public int insertCalendar(int userId, String term, String calendar) {
        Map<String, Object> map = new HashMap<>();
        map.put("userid", userId);
        map.put("term", term);
        map.put("calendar", calendar);
        return calendarMapper.insertCalendar(map);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public int updateCalendar(int userId, List<String> term, List<String> calendar) {
        if (term.size() != calendar.size()) {
            return -1;
        }
        int size = 0;
        Map<String, Object> map = new HashMap<>();
        for (int i = 0, length = term.size(); i < length; i++) {
            if(calendarMapper.getCalendar(userId,term.get(i))!=null){
//                calendarMapper.deleteCalendar(userId,term.get(i));
//                如果calendar存在，不能直接删除课表，必须保持课表信息，此时，提示用户该学期该课表已存在，请在课表页面选择学期直接导入课表。
                return 0;
            }
            map.put("userid", userId);
            map.put("term", term.get(i));
            map.put("calendar", calendar.get(i));
            size += calendarMapper.insertCalendar(map);
            map.clear();
        }
        return size;
    }

    @NotNull
    @Override
    public String shareCalendar(int userId, String calendar) {

        String key = UUID.randomUUID().toString().replace("-", "");
        calendarMapper.deleteSharedCalendar(userId);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("userid", userId);
        map.put("key", key);
        map.put("calendar", calendar);
        int flag = calendarMapper.shareCalendar(map);
        if (flag == 0) {
            return "";
        } else {
            return key;
        }
    }

    @Override
    public String getSharedCalendar(String key) {
        return calendarMapper.getSharedCalendar(key);
    }
}
