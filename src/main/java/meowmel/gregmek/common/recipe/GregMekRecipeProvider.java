package meowmel.gregmek.common.recipe;

import mekanism.api.annotations.NothingNullByDefault;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@NothingNullByDefault
public class GregMekRecipeProvider implements DataProvider { // 实现 DataProvider 接口

    private static final Logger LOGGER = LoggerFactory.getLogger("gregmek-datagen");

    private final PackOutput output;
    private final ExistingFileHelper existingFileHelper;
    private final String modid;

    public GregMekRecipeProvider(PackOutput output, ExistingFileHelper existingFileHelper, String modid) {
        this.output = output;
        this.existingFileHelper = existingFileHelper;
        this.modid = modid;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.runAsync(() -> {
            java.util.concurrent.atomic.AtomicInteger written = new java.util.concurrent.atomic.AtomicInteger();
            Consumer<FinishedRecipe> consumer = recipe -> {
                written.incrementAndGet();
                // saveStable 内部是异步落盘，必须 join，否则数据生成结束后 JVM 退出会丢掉尚未写完的文件
                DataProvider.saveStable(cache, recipe.serializeRecipe(),
                        output.getOutputFolder().resolve("data/" + recipe.getId().getNamespace() +
                                "/recipes/" + recipe.getId().getPath() + ".json"))
                        .join();
            };

            OreProgressHandler.recipeOreAddition(consumer);
            LOGGER.info("[gregmek] 写入配方回调被调用 {} 次", written.get());
        });
    }

    @Override
    public String getName() {
        return modid;
    }
}
