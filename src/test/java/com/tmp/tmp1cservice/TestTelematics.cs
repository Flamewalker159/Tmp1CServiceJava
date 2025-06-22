using System.Net;
using System.Text;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.DependencyInjection.Extensions;
using Moq;
using Newtonsoft.Json;
using Tmp1CService.DTOs.TelematicsDTOs;
using Tmp1CService.Models;
using Tmp1CService.Repositories.Interfaces;

namespace Tmp1CService.Tests;

public class TestTelematics : IClassFixture<CustomWebApplicationFactory<Program>>
{
    private readonly HttpClient _client;
    private readonly AppDbContext _db;
    private readonly CustomWebApplicationFactory<Program> _factory;

    public TestTelematics(CustomWebApplicationFactory<Program> factory)
    {
        _factory = factory;
        _client = factory.CreateClient();
        var options = new DbContextOptionsBuilder<AppDbContext>()
            .UseInMemoryDatabase("TestDb")
            .Options;
        _db = new AppDbContext(options);
    }

    private HttpClient CreateClientWithMock(Mock<ITelematicsRepository> mockRepo)
    {
        return _factory.WithWebHostBuilder(builder =>
        {
            builder.ConfigureServices(services =>
            {
                services.RemoveAll<ITelematicsRepository>();
                services.AddScoped(_ => mockRepo.Object);
            });
        }).CreateClient();
    }

    private static void AddApiKey(HttpClient client)
    {
        client.DefaultRequestHeaders.Add("X-API-KEY", "qweasd");
    }

    private static Client GetTestClient(Guid? id = null)
    {
        return new Client
        {
            Id = id ?? Guid.NewGuid(),
            Login = "TestClient",
            Password = "123",
            Url1C = "http://localhost/1CTest"
        };
    }

    private static async Task<TelematicsDataDto> LoadTelematicsDataAsync(
        string filePath = "TestData/TelematicsData.json")
    {
        var jsonData = await File.ReadAllTextAsync(filePath);
        return JsonConvert.DeserializeObject<TelematicsDataDto>(jsonData)!;
    }

    private static StringContent CreateJsonContent(object data)
    {
        return new StringContent(JsonConvert.SerializeObject(data), Encoding.UTF8, "application/json");
    }

    #region SendTelematicsData

    #region return200

    [Fact]
    public async Task SendingTelematicsData_ReturnsOk()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();

        var telematicsData = await LoadTelematicsDataAsync();
        var vehicleCode1C = "000000001";

        var mockRepo = new Mock<ITelematicsRepository>();
        mockRepo.Setup(repo => repo.SendingTelematicsData(
            It.Is<Client>(u => u.Id == client1C.Id),
            It.Is<string>(v => v == vehicleCode1C),
            It.IsAny<TelematicsDataDto>())).ReturnsAsync(
            new HttpResponseMessage
            {
                StatusCode = HttpStatusCode.OK,
                Content = new StringContent("Данные успешно записаны", Encoding.UTF8, "application/json")
            });

        var client = CreateClientWithMock(mockRepo); // Передаём готовый мок
        AddApiKey(client);
        var content = CreateJsonContent(telematicsData);

        // Act
        var response = await client.PostAsync($"api/clients/{client1C.Id}/telematicsData/{vehicleCode1C}", content);

        // Assert
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
        mockRepo.Verify(repo => repo.SendingTelematicsData(
            It.Is<Client>(c => c.Id == client1C.Id),
            It.Is<string>(v => v == vehicleCode1C),
            It.IsAny<TelematicsDataDto>()), Times.Once());
    }

    #endregion

    #region return400

    [Fact]
    public async Task SendingTelematicsData_ReturnsBadRequest_WhenTelematicsDataIsInvalid()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();

        var invalidTelematicsData = await LoadTelematicsDataAsync("TestData/InvalidTelematicsData.json");
        var vehicleCode1C = "000000001";

        var mockRepo = new Mock<ITelematicsRepository>();
        mockRepo.Setup(repo => repo.SendingTelematicsData(
            It.Is<Client>(u => u.Id == client1C.Id),
            It.Is<string>(v => v == vehicleCode1C),
            It.IsAny<TelematicsDataDto>())).ReturnsAsync(
            new HttpResponseMessage
            {
                StatusCode = HttpStatusCode.BadRequest,
                Content = new StringContent("Ошибка: Некорректные данные", Encoding.UTF8, "application/json")
            });

        var client = CreateClientWithMock(mockRepo);
        AddApiKey(client);
        var content = CreateJsonContent(invalidTelematicsData);

        // Act
        var response = await client.PostAsync($"api/clients/{client1C.Id}/telematicsData/{vehicleCode1C}", content);

        // Assert
        Assert.Equal(HttpStatusCode.BadRequest, response.StatusCode);
        mockRepo.Verify(repo => repo.SendingTelematicsData(
            It.Is<Client>(c => c.Id == client1C.Id),
            It.Is<string>(v => v == vehicleCode1C),
            It.IsAny<TelematicsDataDto>()), Times.Once());
    }

    #endregion

    #region return401

    [Fact]
    public async Task SendingTelematicsData_ReturnsUnauthorized_WhenNoApiKeyProvided()
    {
        // Arrange
        var client1C = GetTestClient();
        await _db.Clients.AddAsync(client1C);
        await _db.SaveChangesAsync();

        var telematicsData = await LoadTelematicsDataAsync();
        var vehicleCode1C = "000000001";

        var client = _factory.CreateClient(); // Без API-ключа
        var content = CreateJsonContent(telematicsData);

        // Act
        var response = await client.PostAsync($"api/clients/{client1C.Id}/telematicsData/{vehicleCode1C}", content);

        // Assert
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }

    #endregion

    #region return404

    [Fact]
    public async Task SendingTelematicsData_ReturnsNotFound_WhenClientDoesNotExist()
    {
        // Arrange
        var telematicsData = await LoadTelematicsDataAsync();
        var nonExistentClientId = Guid.NewGuid();
        var vehicleCode1C = "000000001";

        var client = _factory.CreateClient();
        AddApiKey(client);
        var content = CreateJsonContent(telematicsData);

        // Act
        var response =
            await client.PostAsync($"api/clients/{nonExistentClientId}/telematicsData/{vehicleCode1C}", content);

        // Assert
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }

    #endregion

    #endregion
}