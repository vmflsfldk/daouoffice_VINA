import React from 'react';
import './TagLabel.css';

export default function TagLabel({ tagInfo, className = '' }) {
  if (!tagInfo?.hasTag) {
    return null;
  }

  const {
    prefix,
    suffix,
    hasTrailingHyphen,
    prefixClass,
    suffixClass
  } = tagInfo;

  const wrapperClassName = ['tag-label', className].filter(Boolean).join(' ');

  return (
    <span className={wrapperClassName}>
      <span className="tag-label__bracket">[</span>
      {prefix && (
        <span className={`tag-label__prefix ${prefixClass}`}>{prefix}</span>
      )}
      {(suffix || hasTrailingHyphen) && (
        <span className="tag-label__separator">-</span>
      )}
      {suffix && (
        <span className={`tag-label__suffix ${suffixClass}`}>{suffix}</span>
      )}
      <span className="tag-label__bracket">]</span>
    </span>
  );
}
