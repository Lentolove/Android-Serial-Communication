cmake_minimum_required(VERSION 3.10.2)

# Declares and names the project.

project("serial_port2")

add_library( # Sets the name of the library.
             serial

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             SerialPort.c)

include_directories(src/main/jni/)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )

target_link_libraries( # Specifies the target library.
        serial

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )