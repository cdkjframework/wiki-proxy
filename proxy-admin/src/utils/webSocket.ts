let baseUrl = process.env.NODE_ENV === 'production' ?
  window.location.origin.replace('http', 'ws') :
  'ws://127.0.0.1:10101'
  // 'wss://wss.langzhiyun.net'

interface Options {
  // 地址
  url: string,
  path: string,
  service: string,
  // 请求ID
  socketId: string
}

type WebSocketPayload = object | string | ArrayBuffer | Blob

interface WebSocketLike {
  close(): any;

  send(data: WebSocketPayload): any;

  onopen: ((event: any) => any) | null;
  onclose: ((event: any) => any) | null;
  onmessage: ((event: any) => any) | null;
  onerror: ((event: any) => any) | null;
  readyState: number;
}

const defaultOptions = {
  // 地址
  url: '',
  path: '/pms',
  service: '/socket/webSocket/real_time',
  // 重连接时间间隔
  reconnectInterval: 30 * 1000,
  // 心跳时间间隔
  heartTime: 3000,
  // 消息回调
  onMessage: (message: object) => {
    console.log(message)
  },
  // 连接打开
  onOpen: () => {
    console.log('onOpen')
  },
  // 错误信息
  onError: () => {
    console.log('onError')
  }
};

class WebSocketClass {
  private ws: WebSocketLike | null;
  private readonly url: string;
  private readonly reconnect: boolean;
  private readonly reconnectInterval: number;
  private readonly heartTime: number;
  private readonly option: any = {};
  private reconnectTimeOut: null | any;
  private heart: boolean;
  private isDestroy: boolean;
  private heartTimeout: null | any;
  private isReconnected: boolean;

  constructor(options: object | Options) {
    this.option = Object.assign({}, defaultOptions, options);
    if (!this.option.url) {
      this.option.url = baseUrl
    }
    const uri = this.option.url + this.option.path + this.option.service
    this.option.uri = uri
    this.url = uri;
    this.reconnect = true;
    this.reconnectInterval = 30 * 1000;
    this.isDestroy = false;

    this.ws = null
    this.heart = false;
    this.heartTime = 3000 as any;
    // 心跳信息
    this.heartTimeout = null;
    // 是否在重连中
    this.isReconnected = true;

    // 创建连接
    this.connect(this.option)
  }

  // 初始化
  connect(option: any) {
    this.ws = new WebSocket(this.url);
    this.ws!.onopen = option.onOpen

    this.ws!.onmessage = (event: any) => {
      // 过滤心跳数据
      const data = JSON.parse(event.data) || {};
      if (data.type === 'heartbeat') {
        this.sendHeart();
        return
      }
      if (!this.heart) {
        this.sendHeart();
      }
      option.onMessage(data)
    };

    this.ws!.onclose = () => {
      option.onError()
      this.doReconnect();
    };

    this.ws!.onerror = () => {
      option.onError()
      this.doReconnect();
    };
  }

  // 销毁当前websocket
  destroy() {
    this.isDestroy = true;

    if (this.ws) this.ws.close();
    this.ws = null;

    // 清除延时
    clearTimeout(this.heartTimeout!);
    this.heartTimeout = null;
  }

  // 重连
  doReconnect() {
    // 是否允许重新连接
    if (this.reconnect) {
      // 存在ws 则先关闭
      if (this.ws) {
        this.ws.close();
        this.ws = null;
      }

      // 主动destroy，不进行重连
      if (this.isDestroy) return;

      // 重连判断 在重连中则返回
      if (this.isReconnected) return;
      this.isReconnected = true;

      const time = this._generateInterval();
      this.reconnectTimeOut = setTimeout(() => {
        this.isReconnected = false;
        this.connect(this.option);
      }, time);
    }
  }

  // 监听心跳
  sendHeart() {
    this.heart = true
    clearTimeout(this.heartTimeout!);
    if (!this.isDestroy) { // ws 已经删除不做心跳重连
      this.heartTimeout = setTimeout(() => {
        // 发送心跳
        if (this.ws && this.ws.readyState === 1) {
          this.sendMessage({type: 'heartbeat'});
        }
        clearTimeout(this.heartTimeout)
      }, this.heartTime);
    } else {
      clearTimeout(this.heartTimeout)
    }
  }

  // websocket 发送消息
  sendMessage(message: object | string) {
    if (!message || !this.ws) {
      return
    }
    try {
      this.ws.send(JSON.stringify(message));
    } catch (err) {
      const timer = setInterval(() => {
        if (this.ws && this.ws.readyState === 1) {
          this.ws.send(JSON.stringify(message));
          clearInterval(timer);
        }
      }, 200);
    }
  }

  // 重连时间间隔控制
  _generateInterval(k: number = 300) {
    if (this.reconnectInterval > 0) {
      return this.reconnectInterval;
    }
    return Math.min(30, (Math.pow(2, k) - 1)) * 1000;
  }
}

const wsSocket = (options: object | Options) => new WebSocketClass(options);

export {
  wsSocket,
};