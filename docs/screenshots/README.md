# 截图素材

README「效果展示」区引用的图片都在这里，**已入库**（`*.webp`）。

```
docs/screenshots/
├── 01-wan-jian-gui-zong.webp   … 12-immovable-king.webp   # 静态峰值帧，980×~580，约 50-70 KB
└── anim/
    ├── 01-wan-jian-gui-zong.webp                          # 动态演示，520 宽，~0.5-1.1 MB
    ├── 02-senbonzakura.webp
    ├── 09-qi-sha-jian-jie.webp
    ├── 10-blood-moon-slash.webp
    ├── 11-thors-wrath.webp
    └── 12-immovable-king.webp
```

## 素材来源

原始素材是游戏内录制（SimpleRecorder，动态 AVIF，1280×747、50~170 帧）。
处理流程：

1. 用 Pillow 逐帧解码（ffmpeg 只能读到第一帧，不可用）；
2. 在片段的 12%~48% 区间里按「细节多、不过曝」挑峰值帧 → 静态图；
3. 裁掉底部物品栏与右侧聊天栏，缩放到 980 宽 → WebP q80；
4. 6 个代表性 SA 额外导出 520 宽动态 WebP（12.5 fps，q52）。

重新生成（需要 Python + Pillow 11+）：

```bash
python make_shots.py 素材目录 docs/screenshots docs/screenshots/anim
```

## 已知问题

- 录制画面里带 Xaero 小地图和聊天栏，裁切后仍有残留；正式发布建议 `F1` 关 HUD 并关闭小地图后重拍。
- 部分效果（不动明王、千本樱）在低码率 AVIF 上偏曝，静态图偏白；重拍时把 `[vfx] particleScale` 调到 0.7 左右会更好看。