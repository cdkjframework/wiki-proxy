export const loadAmap = {
  key: '9072b65e65cdd79da6631d9664658d08',  //设置您的key
  version: "2.0",
  plugins: ['AMap.Autocomplete', 'AMap.PlaceSearch', 'AMap.Scale', 'AMap.OverView', 'AMap.ToolBar', 'AMap.MapType', 'AMap.PolyEditor'],
  AMapUI: {
    version: "1.1",
    plugins: [],
  },
  Loca: {
    version: "2.0.0"
  },
}

export const options = {
  viewMode: '3D',
  zoom: 15,
  zooms:[2,22],
  center: [104.056299, 30.537293]
}

export const autoOptions = {
  //city 限定城市，默认全国
  city: '成都'
}