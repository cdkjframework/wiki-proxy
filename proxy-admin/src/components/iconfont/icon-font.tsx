import {computed, defineComponent, PropType, unref} from 'vue';
import {createFromIconfontCN} from '@ant-design/icons-vue';
import {isString} from '@/utils/is';

let IconFont = createFromIconfontCN({
  scriptUrl: 'iconfont.js',
});

export default defineComponent({
  name: 'IconFont',

  props: {
    type: {
      type: String as PropType<string>,
      default: '',
    },
    prefix: {
      type: String,
      default: 'icon-',
    },
    color: {
      type: String as PropType<string>,
      default: 'unset',
    },
    size: {
      type: [Number, String] as PropType<number | string>,
      default: 14,
    },
    scriptUrl: {
      // 阿里图库字体图标路径
      type: String as PropType<string | string[]>,
      default: '',
    },
  },
  setup(props, {attrs}) {
    // 如果外部传进来字体图标路径，则覆盖默认的
    let scriptUrls = [] as any;
    if (props.scriptUrl) {
      scriptUrls = [...new Set(scriptUrls.concat(props.scriptUrl))] as any;
      IconFont = createFromIconfontCN({
        scriptUrl: scriptUrls,
      });
    }

    const wrapStyleRef = computed(() => {
      const {color, size} = props;

      const fs = isString(size) ? parseFloat(size) : size;

      return {
        color,
        fontSize: `${fs}px`,
      };
    });

    return () => {
      const {type, prefix} = props;

      return type ? (
        <IconFont
          type={type.startsWith(prefix) ? type : `${prefix}${type}`}
          {...attrs}
          style={unref(wrapStyleRef)}
        />
      ) : null;
    };
  },
});
