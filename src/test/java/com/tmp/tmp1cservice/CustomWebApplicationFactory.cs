using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.Extensions.DependencyInjection;
using Tmp1CService.Models;

namespace Tmp1CService.Tests;

public class CustomWebApplicationFactory<TProgram>
    : WebApplicationFactory<TProgram> where TProgram : class
{
    protected override void ConfigureWebHost(IWebHostBuilder builder)
    {
        builder.ConfigureServices(services =>
        {
            // Удаление стандартной конфигурации бд
            var dbContextDescriptor = services.SingleOrDefault(d => d.ServiceType ==
                                                                    typeof(IDbContextOptionsConfiguration<
                                                                        AppDbContext>));

            services.Remove(dbContextDescriptor!);

            // Добавляем новый контекст с InMemoryDatabase
            services.AddDbContext<AppDbContext>(options => { options.UseInMemoryDatabase("TestDb"); });

            // Создаём базу перед каждым тестом
            var sp = services.BuildServiceProvider();
            using var scope = sp.CreateScope();
            var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
            db.Database.EnsureCreated();
        });

        builder.UseEnvironment("Development");
    }
}