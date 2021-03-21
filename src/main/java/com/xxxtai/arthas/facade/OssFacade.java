package com.xxxtai.arthas.facade;

import com.intellij.openapi.project.Project;
import com.xxxtai.arthas.domain.Result;

/**
 * Copyright (c) 2020, 2021, xxxtai. All rights reserved.
 *
 * @author xxxtai
 */
public interface OssFacade {

    Result<String> uploadString(Project project, String key, String content);
}
