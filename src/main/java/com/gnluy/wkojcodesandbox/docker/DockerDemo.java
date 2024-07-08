package com.gnluy.wkojcodesandbox.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.PingCmd;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;

import java.util.List;

public class DockerDemo {

    public static void main(String[] args) throws InterruptedException {

        //配置docker服务端地址
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://192.168.107.128:2375")
                .build();
        // 获取 Docker Client
        DockerClient dockerClient = DockerClientBuilder.getInstance(config).build();
        PingCmd pingCmd = dockerClient.pingCmd();

        //拉取镜像
        String image = "nginx:latest";
//        pullImage(image, dockerClient);


        //创建容器
//        CreateContainerCmd containerCmd = dockerClient.createContainerCmd(image);
//        CreateContainerResponse createContainerResponse = containerCmd
//                .withCmd("echo", "Hello Docker")
//                .exec();
//        System.out.println(createContainerResponse);
//        String containerId = createContainerResponse.getId();

        //列出所有容器
        final ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        final List<Container> containers = listContainersCmd.withShowAll(true).exec();
        if (containers.size() == 0) System.out.println("无");
        String containerId = null;
        for (Container container: containers) {
            System.out.println(container);
            containerId = container.getId();
        }

        // 启动容器
        //dockerClient.startContainerCmd(containerId).exec();

        //删除容器
        //dockerClient.removeContainerCmd(containerId).exec();

        //删除镜像
        //dockerClient.removeImageCmd("fffffc90d343").exec();

    }
    //拉取镜像
    public static void pullImage(String image, DockerClient dockerClient) {
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(image);
        PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                System.out.println("下载镜像：" + item.getStatus());
                super.onNext(item);
            }
            @Override
            public void onComplete() {
                System.out.println("镜像拉取完成");
                super.onComplete();
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println("拉取镜像时出错：" + throwable.getMessage());
                super.onError(throwable);
            }
        };
        try {
            pullImageCmd
                    .exec(pullImageResultCallback)
                    .awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("下载完成");
    }
}
