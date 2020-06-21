package com.xxxtai.arthas.facade;

import com.xxxtai.arthas.domain.Result;

public interface OssFacade {

    Result<String> uploadString(String key, String content);
}
