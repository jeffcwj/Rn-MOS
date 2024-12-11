//
// Created by 33688 on 2024/12/4.
//

#ifndef RNMOS_JAVA_H
#define RNMOS_JAVA_H


class Java {

public:
    Java(JavaVM *g_java_vm, JNIEnv *env);

    std::string getFlavor();
    std::vector<std::string> initMasterServers();
    std::vector<std::string> getMasterServers() {
        return m_masterServers;
    };
    void setupContext(jobject thiz, JNIEnv *env);
    jobject getContext();
    void showPasswordDialog(const char* password);

private:

    JNIEnv *m_Env = nullptr;
    std::string m_flavor;
    std::vector<std::string> m_masterServers;
    jobject activity;
    JavaVM* javaVM;

    jmethodID m_passwordDialog;

    JNIEnv *getEnv();
};


#endif //RNMOS_JAVA_H
