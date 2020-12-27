package com.xxxtai.arthas.facade;

import com.intellij.openapi.project.Project;
import com.xxxtai.arthas.domain.Result;

public interface OssFacade {

    Result<String> uploadString(Project project, String key, String content);
}
