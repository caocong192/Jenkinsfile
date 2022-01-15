#!/usr/bin/env python
# -*- coding: UTF-8 -*-

import configparser
import os
import sys


def get_env_value():
    config = configparser.ConfigParser()
    config.read("./Project_Infos.ini", encoding='utf-8')

    if section not in config:
        print("没有获取到 {} cfg配置信息.".format(Param))
        exit(1)

    cfg = config[section]
    if Param in cfg.keys():
        print(cfg[Param])
        return cfg[Param]
    else:
        print("没有获取到 {} Param配置信息.".format(Param))
        exit(2)


if __name__ == '__main__':
    Param = sys.argv[1]
    ProjectVersion = sys.argv[2]
    if len(sys.argv) == 4:
        BranchName = sys.argv[3]
        section = ".".join([ProjectVersion, BranchName])
    else:
        section = ProjectVersion
    get_env_value()
