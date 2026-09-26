const unwrapHtmlLayers = {
  postcssPlugin: 'unwrap-html-layers',
  Once(root) {
    if (!root.source?.input.file?.endsWith('.html')) return

    root.walkAtRules('layer', (layer) => {
      if (layer.params !== 'base' || !layer.parent) return

      const parent = layer.parent
      layer.nodes?.slice().forEach((node) => parent.insertBefore(layer, node))
      layer.remove()
    })
  },
}

module.exports = {
  plugins: [
    unwrapHtmlLayers,
    require('tailwindcss'),
    require('autoprefixer'),
  ],
}
