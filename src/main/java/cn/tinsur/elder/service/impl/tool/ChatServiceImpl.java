/*
 *
 *  * ============================================================
 *  *
 *  *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *  *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *  *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *  *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *  *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *  *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *  *
 *  *  项目名称 : 智慧社区养老系统
 *  *  源码作者 : Tinsur (tinsur.cn)
 *  *  作者主页 : https://www.tinsur.cn
 *  *  联系方式 : me@tinsur.cn
 *  *  开源协议 : GPL 3.0
 *  *
 *  * ============================================================
 *
 */

/*
 *
 *  * ============================================================
 *  *
 *  *   ████████╗██╗███╗   ██╗███████╗██╗   ██╗██████╗
 *  *   ╚══██╔══╝██║████╗  ██║██╔════╝██║   ██║██╔══██╗
 *  *      ██║   ██║██╔██╗ ██║███████╗██║   ██║██████╔╝
 *  *      ██║   ██║██║╚██╗██║╚════██║██║   ██║██╔══██╗
 *  *      ██║   ██║██║ ╚████║███████║╚██████╔╝██║  ██║
 *  *      ╚═╝   ╚═╝╚═╝  ╚═══╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝
 *  *
 *  *  项目名称 : 智慧社区养老系统
 *  *  源码作者 : Tinsur (tinsur.cn)
 *  *  作者主页 : https://www.tinsur.cn
 *  *  联系方式 : me@tinsur.cn
 *  *  开源协议 : GPL 3.0
 *  *
 *  * ============================================================
 *
 */

package cn.tinsur.elder.service.impl.tool;

import cn.tinsur.elder.service.IChatService;
import cn.tinsur.elder.service.ICarePlanService;
import cn.tinsur.elder.service.IExamAppointmentService;
import cn.tinsur.elder.service.IElderService;
import cn.tinsur.elder.tools.ElderTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;

@Service
@Slf4j
public class ChatServiceImpl implements IChatService {

    private static final String STREAM_END_MARK = "[END]";
    private static final String EMPTY_INPUT_REPLY = "请输入您想咨询的问题";
    private static final String ERROR_REPLY = "抱歉，小邻暂时无法回复，请稍后再试。";

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private IElderService elderService;

    @Autowired
    private IExamAppointmentService examAppointmentService;

    @Autowired
    private ICarePlanService carePlanService;

    @Override
    public String chat(String message, Integer conversationId) {
        if (ObjectUtils.isEmpty(message)) {
            return EMPTY_INPUT_REPLY;
        }
        try {
            return chatClient.prompt()
                    .user(message)
                    // 会话记忆按 conversationId（老人id）隔离，避免不同用户上下文串扰
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                    // 注册AI工具：老人问自己的信息/体检记录/护理计划时，模型会自主调用查库
                    .tools(new ElderTools(conversationId, elderService, examAppointmentService, carePlanService))
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("AI调用失败, conversationId: {}", conversationId, e);
            return ERROR_REPLY;
        }
    }

    @Override
    public Flux<String> chatStream(String message, Integer conversationId) {
        if (ObjectUtils.isEmpty(message)) {
            return Flux.just(EMPTY_INPUT_REPLY, STREAM_END_MARK);
        }
        return chatClient.prompt()
                .user(message)
                // 会话记忆按 conversationId（老人id）隔离，避免不同用户上下文串扰
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                // 注册AI工具：老人问自己的信息/体检记录/护理计划时，模型会自主调用查库
                .tools(new ElderTools(conversationId, elderService, examAppointmentService, carePlanService))
                .stream()
                .content()
                // 模型调用失败时也要发出提示和结束标记，避免前端一直处于等待状态
                .onErrorResume(e -> {
                    log.error("AI流式调用失败, conversationId: {}", conversationId, e);
                    return Flux.just(ERROR_REPLY, STREAM_END_MARK);
                })
                // 在流结束时添加结束标记
                .concatWith(Flux.just(STREAM_END_MARK));
    }
}