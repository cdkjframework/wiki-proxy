import CryptoJS from 'crypto-js';

/**
 * 加密--应和后台java解密或是前台js解密的密钥保持一致（16进制）
 * */
const KEY = CryptoJS.enc.Utf8.parse('cn.framewiki.com');
const IV = CryptoJS.enc.Utf8.parse('hk.framewiki.com');

/**
 * AES加密 ： 字符串 key iv  返回base64
 *
 */
export default {
  encrypt(word: any) {
    const key = KEY;
    const iv = IV;
    // 偏移量
    const encryption = CryptoJS.enc.Utf8.parse(word);
    // 算法
    const encrypted = CryptoJS.AES.encrypt(encryption, key, {
      iv,
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.ZeroPadding,
    });
    return CryptoJS.enc.Base64.stringify(encrypted.ciphertext);
  },
  /**
   * AES 解密 ：字符串 key iv  返回base64
   *
   */
  decrypt(word: any) {
    const key = KEY;
    const iv = IV;
    const base64 = CryptoJS.enc.Base64.parse(word);
    const base64Value = CryptoJS.enc.Base64.stringify(base64);
    // AES解密
    const decrypt = CryptoJS.AES.decrypt(base64Value, key, {
      iv,
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.ZeroPadding,
    });
    return CryptoJS.enc.Utf8.stringify(decrypt).toString();
  },
};