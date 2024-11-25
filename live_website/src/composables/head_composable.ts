import { ref, onMounted, onUnmounted, watch } from 'vue'

export function useMinecraftHead(skinUrl: string, size: number) {
  const headUrl = ref(null);
  const canvas = ref(null);

  const extractHead = () => {
    const ctx = (canvas.value as any).getContext('2d');
    const skin = new Image();
    skin.crossOrigin = 'anonymous';
    skin.src = skinUrl;

    skin.onload = () => {
      ctx.imageSmoothingEnabled = false
      // Draw the skin onto the canvas
      ctx.drawImage(skin, 8, 8, 8, 8, 0, 0, size, size);

      // Extract the head part as a base64 image
      headUrl.value = (canvas.value as any).toDataURL('image/png');
    };
  };

  watch(canvas, newCanvas => {
    if(newCanvas) {
      extractHead()
    }
  })

  return {
    headUrl,
    canvas,
  };
}