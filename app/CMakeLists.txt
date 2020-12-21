cmake_minimum_required(VERSION 3.4.1)

add_library(native-lib STATIC src/native/data_manager.cpp src/native/data_api.cpp)

include_directories(src/native/)
