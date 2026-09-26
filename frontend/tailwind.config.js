/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        "error-container":"#ffdad6","on-primary-fixed":"#002106","surface-container-lowest":"#ffffff","inverse-surface":"#2a313d","secondary-container":"#e2e2e5","primary":"#006e24","outline-variant":"#bbcbb6","outline":"#6c7b69","surface-container-highest":"#dce2f3","secondary-fixed":"#e2e2e5","error":"#ba1a1a","inverse-on-surface":"#ebf1ff","on-primary":"#ffffff","on-tertiary":"#ffffff","background":"#f9f9ff","on-secondary-container":"#636467","secondary":"#5d5e61","surface-bright":"#f9f9ff","on-surface-variant":"#3c4b3a","on-tertiary-fixed-variant":"#434846","on-error-container":"#93000a","secondary-fixed-dim":"#c6c6c9","on-tertiary-fixed":"#181c1b","tertiary-container":"#babebc","tertiary":"#5b5f5e","surface-variant":"#dce2f3","on-background":"#151c27","surface-tint":"#006e24","on-primary-fixed-variant":"#005319","surface":"#f9f9ff","primary-container":"#0bda51","primary-fixed":"#6dff80","primary-fixed-dim":"#27e45a","surface-container-low":"#f0f3ff","tertiary-fixed":"#e0e3e1","on-error":"#ffffff","on-tertiary-container":"#494d4c","on-surface":"#151c27","surface-container":"#e7eefe","on-secondary":"#ffffff","surface-container-high":"#e2e8f8","on-primary-container":"#00591b","inverse-primary":"#27e45a","on-secondary-fixed-variant":"#454749","tertiary-fixed-dim":"#c4c7c5","on-secondary-fixed":"#1a1c1e"
      },
      borderRadius: {
        "DEFAULT":"0.125rem","lg":"0.25rem","xl":"0.5rem","full":"0.75rem"
      },
      spacing: {
        "sm":"16px","base":"4px","margin-desktop":"48px","md":"24px","xs":"8px","xl":"64px","margin-mobile":"16px","lg":"40px","gutter":"24px"
      },
      fontFamily: {
        "body-lg":["Hanken Grotesk"],"label-sm":["JetBrains Mono"],"body-md":["Hanken Grotesk"],"headline-lg-mobile":["Hanken Grotesk"],"button":["Hanken Grotesk"],"headline-md":["Hanken Grotesk"],"headline-lg":["Hanken Grotesk"]
      },
      fontSize: {
        "body-lg":["18px",{"lineHeight":"28px","fontWeight":"400"}],"label-sm":["12px",{"lineHeight":"16px","letterSpacing":"0.05em","fontWeight":"500"}],"body-md":["16px",{"lineHeight":"24px","fontWeight":"400"}],"headline-lg-mobile":["32px",{"lineHeight":"38px","letterSpacing":"-0.01em","fontWeight":"700"}],"button":["16px",{"lineHeight":"1","fontWeight":"600"}],"headline-md":["24px",{"lineHeight":"32px","fontWeight":"600"}],"headline-lg":["40px",{"lineHeight":"48px","letterSpacing":"-0.02em","fontWeight":"700"}]
      }
    }
  },
  plugins: [],
}
