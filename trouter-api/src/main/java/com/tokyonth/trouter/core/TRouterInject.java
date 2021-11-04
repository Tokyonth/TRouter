package com.tokyonth.trouter.core;

import com.tokyonth.trouter.annotation.TRouteMeta;

import java.util.Map;

public interface TRouterInject {

    void onLoader(Map<String, TRouteMeta> holders);

}
