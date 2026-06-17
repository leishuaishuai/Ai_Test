
package com.language.learn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.language.learn.entity.UserDailySign;

import java.time.LocalDate;
import java.util.Map;

public interface DailySignService extends IService<UserDailySign> {

    Map<String, Object> sign(Long userId);

    boolean isSignedToday(Long userId);

    int getContinuousSignDays(Long userId);

    int getTotalSignDays(Long userId);
}
