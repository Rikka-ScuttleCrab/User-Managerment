const LANG_KEY = "app_language";
function getCurrentLanguage() {
  return (
    localStorage.getItem(LANG_KEY)
    || "vi"
  );
}
function setLanguage(lang) {
  localStorage.setItem(
    LANG_KEY,
    lang
  );
  applyTranslations();
}
function getTranslations() {
  const lang =
    getCurrentLanguage();
  return lang === "en"
    ? window.TRANSLATIONS_EN
    : window.TRANSLATIONS_VI;
}
function applyTranslations() {
  const t = getTranslations();
  // TEXT
  document
    .querySelectorAll("[data-i18n]")
    .forEach(el => {
      const key =
        el.dataset.i18n;
      if (t[key]) {
        el.innerText = t[key];
      }
    });
  // PLACEHOLDER
  document
    .querySelectorAll(
      "[data-i18n-placeholder]"
    )
    .forEach(el => {
      const key =
        el.dataset.i18nPlaceholder;
      if (t[key]) {
        el.placeholder = t[key];
      }
    });
}