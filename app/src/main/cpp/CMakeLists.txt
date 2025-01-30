project(CSMOS)

set(CMAKE_C_STANDARD 11)
set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

cmake_minimum_required(VERSION 3.22.1)

if (NOT ANDROID_ABI STREQUAL "arm64-v8a")
    message(STATUS "Unsupported ABI: ${ANDROID_ABI}")
    return()
endif()
add_subdirectory(csmos)