type Asset = {
  designer: string
  trim: string
  group?: string[]
  fileName?: string
}

function importAll(r: __WebpackModuleApi.RequireContext) {
  return r.keys().map((item): Asset => {
    const itemProps = item.split('/')
    const designer = itemProps.at(1) ?? 'anonymous'
    const fileName = itemProps.at(-1) ?? 'trim.png'

    return {
      designer,
      trim: r(item),
      fileName
    }
  });
}

export const assets: Asset[] = importAll(require.context('./custom-trims/', true, /\.(png|jpe?g|svg)$/))
