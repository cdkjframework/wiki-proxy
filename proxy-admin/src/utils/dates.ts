import moment from "moment";

const DATE_TIME_FORMAT = 'YYYY-MM-DD HH:mm:ss';
const DATE_TIME_FORMAT_CN = 'YYYY年MM月DD日 HH:mm:ss';
const DATE_FORMAT = 'YYYY-MM-DD';
const TIME_FORMAT = 'HH:mm:ss';

export function formatToDateTime(date: Date = new Date(), format: string = DATE_TIME_FORMAT) {
  return moment(date).format(format)
}

export function formatToDateTimeCn(date: Date = new Date(), format: string = DATE_TIME_FORMAT_CN) {
  return moment(date).format(format)
}

export function formatToDate(date: Date = new Date(), format: string = DATE_FORMAT) {
  return moment(date).format(format)
}

export function dateTimeToNumber(date: string) {
  return Date.parse(date);
}

/**
 * 值转换为时分秒
 * @param value 值限秒
 */
export function numberToTime(value: number) {
  let time = moment.duration(value, 'seconds')
  // @ts-ignore
  return moment({h: time.hours(), m: time.minutes(), s: time.seconds()}).format(TIME_FORMAT)
}

/**
 * 值转换为年月日时分秒
 * @param value 值限秒
 */
export function numberToDateTime(value: number) {
  if (value.toString().length == 10) {
    value = value * 1000
  }
  return moment(value).format(DATE_TIME_FORMAT)
}

export function formatToWeek(date: Date = new Date()) {
  const week = moment(date).day()
  switch (week) {
    case 1:
      return '星期一'
    case 2:
      return '星期二'
    case 3:
      return '星期三'
    case 4:
      return '星期四'
    case 5:
      return '星期五'
    case 6:
      return '星期六'
    case 0:
      return '星期日'
  }
}