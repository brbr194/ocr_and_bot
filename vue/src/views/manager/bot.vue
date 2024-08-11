<template>
  <div class="chat-bot">
    <el-header class="chat-bot-header">
      <img src="@/assets/imgs/logo.png" alt="Logo" class="logo" style="width: 30px; height: 30px"/>
      智能问答助手
    </el-header>
    <el-main class="chat-bot-messages">
      <div v-for="(message, index) in messages" :key="index" :class="['message-wrapper', message.sender]">
        <div :class="['message-avatar', message.sender]">
          <div class="message-avatar-inner"></div>
          <div class="userName">{{ message.sender === 'bot' ? 'bot' : userName }}</div>
        </div>
        <div class="message" v-html="message.content"></div> <!-- 使用 v-html 以支持 Markdown -->
      </div>
    </el-main>
    <el-footer class="chat-bot-input">
      <el-input v-model="inputValue" placeholder="输入你想问的问题" @keyup.enter="handleSend"></el-input>
      <el-button :loading="loading" :disabled="!inputValue.trim()" @click="handleSend">Send</el-button>
    </el-footer>
  </div>
</template>

<script>
import { ref } from 'vue';
import markdownit from 'markdown-it';

export default {
  name: 'ChatBot',
  setup() {
    const userName = 'user';
    const inputValue = ref('');
    const messages = ref([]);
    const loading = ref(false);
    const md = markdownit();

    function appendToMessage(messageTxt, sender, isPartial = false) {
      const htmlContent = md.renderInline(messageTxt);
      if (isPartial && messages.value.length > 0 && messages.value[messages.value.length - 1].sender === sender) {
        messages.value[messages.value.length - 1].content += htmlContent;
      } else {
        messages.value.push({ sender, content: htmlContent });
      }
    }

    function handleSend() {
      const userInput = inputValue.value.trim();
      if (userInput) {
        appendToMessage(userInput, 'user');
        inputValue.value = '';
        loading.value = true;

        let botMessageIndex = messages.value.length; // 保存当前bot消息的索引

        fetch(`http://localhost:9090/events/streamAsk?q=${encodeURIComponent(userInput)}`, {
          headers: {
            'Content-Type': 'text/event-stream',
          },
        })
            .then(response => {
              const reader = response.body.getReader();
              return new ReadableStream({
                start(controller) {
                  function push() {
                    reader.read().then(({ done, value }) => {
                      if (done) {
                        controller.close();
                        loading.value = false;
                        return;
                      }
                      const text = new TextDecoder().decode(value);
                      const lines = text.split('\n');
                      lines.forEach(line => {
                        if (line.startsWith('data:')) {
                          const message = line.replace('data:', '').trim();
                          if (messages.value.length === botMessageIndex) {
                            appendToMessage(message, 'bot');
                          } else {
                            appendToMessage(message, 'bot', true);
                          }
                        }
                      });
                      push();
                    }).catch(err => {
                      console.log('Fetch error:', err);
                      loading.value = false;
                    });
                  }
                  push();
                }
              });
            })
            .catch(() => {
              loading.value = false;
              appendToMessage('无法连接到服务器，请稍后再试。', 'bot');
            });
      }
    }

    return { userName, inputValue, messages, loading, handleSend };
  }
};
</script>

<style scoped>
.chat-bot {
  display: flex;
  flex-direction: column;
  width: 100%;
  max-width: 800px;
  margin: 50px auto;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  overflow: hidden;
  font-family: "Roboto", sans-serif;
  background: #fff;
  border: 1px solid #eaeaea;
  max-height: 600px; /* 限制整体高度 */
}

.chat-bot-header {
  background: #fff;
  color: #333;
  text-align: center;
  padding: 20px;
  font-size: 18px;
  border-bottom: 1px solid #eaeaea;
}

.chat-bot-messages {
  flex: 1;
  padding: 20px;
  min-height: 400px;
  overflow-y: auto; /* 允许上下滚动 */
  background: #f9f9f9;
}

.message-wrapper {
  display: flex;
  margin-bottom: 10px;
  align-items: flex-start; /* 确保头像和用户名垂直对齐 */
}

.message-wrapper.user {
  justify-content: flex-end;
}

.message-wrapper.bot {
  justify-content: flex-start;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: url('@/assets/imgs/logo.png'); /* 替换为实际的头像URL */
  background-size: cover;
  flex-shrink: 0; /* 确保头像不会缩小 */
}

.message-content {
  display: flex;
  flex-direction: column;
  max-width: 70%; /* 确保消息不太宽 */
}

.username {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 5px; /* 确保用户名和消息之间有间距 */
}

.message {
  padding: 10px 15px;
  border-radius: 20px;
  font-size: 16px;
  word-break: break-word;
  display: inline-block;
}

.user .message {
  background-color: #e9eff5;
}

.bot .message {
  background-color: #f5f5f5;
}

.message-wrapper.user .message-avatar {
  order: 2; /* 将用户头像放在右侧 */
  margin-left: 10px;
  margin-right: 0;
}

.message-wrapper.bot .message-avatar {
  order: 0; /* 将机器人头像放在左侧 */
  margin-right: 10px;
}

.message-wrapper.user .message-content {
  order: 1; /* 将用户消息内容放在左侧 */
  margin-right: 10px; /* 确保消息内容和头像之间有间距 */
}

.message-wrapper.bot .message-content {
  order: 1; /* 将机器人消息内容放在右侧 */
  margin-left: 10px; /* 确保消息内容和头像之间有间距 */
}

.chat-bot-input {
  display: flex;
  align-items: center;
  border-top: 1px solid #ccc;
  padding: 10px;
  background-color: #fff;
}

.chat-bot-input input {
  flex: 1;
  padding: 10px 15px;
  border: none;
  border-radius: 20px;
  font-size: 16px;
  outline: none;
  margin-right: 10px;
}

.chat-bot-input button {
  padding: 10px 20px;
  background-color: #007bff;
  border: none;
  border-radius: 50px;
  color: white;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.chat-bot-input button:hover {
  background-color: #0056b3;
}
</style>
